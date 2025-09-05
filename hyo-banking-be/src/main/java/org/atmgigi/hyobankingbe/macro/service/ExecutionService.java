package org.atmgigi.hyobankingbe.macro.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.atmgigi.hyobankingbe.common.exception.DomainException;
import org.atmgigi.hyobankingbe.common.exception.ErrorCode;
import org.atmgigi.hyobankingbe.macro.domain.ExecutionStatus;
import org.atmgigi.hyobankingbe.macro.dto.MacroResponseDTOs.*;
import org.atmgigi.hyobankingbe.macro.entity.MacroExecution;
import org.atmgigi.hyobankingbe.macro.entity.MacroQrToken;
import org.atmgigi.hyobankingbe.macro.entity.MacroStep;
import org.atmgigi.hyobankingbe.macro.repository.MacroExecutionRepository;
import org.atmgigi.hyobankingbe.macro.repository.MacroQrTokenRepository;
import org.atmgigi.hyobankingbe.macro.repository.MacroRepository;
import org.atmgigi.hyobankingbe.macro.repository.MacroStepRepository;
import org.atmgigi.hyobankingbe.txn.dto.TxnRequestDTO;
import org.atmgigi.hyobankingbe.txn.service.TxnService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Log4j2
@RequiredArgsConstructor
public class ExecutionService {

    private final MacroExecutionRepository macroExecutionRepository;
    private final MacroQrTokenRepository macroQrTokenRepository;
    private final TxnService txnService;
    private final MacroStepRepository macroStepRepository;
    private final MacroRepository macroRepository;

    // 실행 세션 시작 (토큰 1회성 보장)
    @Transactional
    public MacroExecutionResponseDTO start(Long macroId, String token) {
        // 1) (macro_id, token)으로 토큰 조회
        MacroQrToken macroQrToken = macroQrTokenRepository.findByMacro_IdAndToken(macroId, token)
                .orElseThrow(()-> new DomainException(ErrorCode.RESOURCE_NOT_FOUND,"토큰이 조회되지 않습니다."));

        // 2) 유효성 (만료/사용됨) 체크
        LocalDateTime now = LocalDateTime.now();
        if (macroQrToken.getUsedAt() != null || macroQrToken.getExpiresAt().isBefore(now)){
            throw new DomainException(ErrorCode.VALIDATION_FAILED,"토큰이 유효하지 않습니다.");
        }

        // 3) 사용 처리 (재사용 방지)
        macroQrToken.setUsedAt(now);

        try {
            // 4) 실행 엔티티 생성 (토큰과 매크로는 엔티티로 연결)
            MacroExecution ex = MacroExecution.builder()
                    .macro(macroRepository.getReferenceById(macroId)) // // FK: macro_id
                    .qrToken(macroQrToken) // FK: qr_token_id (unique)
                    .status(ExecutionStatus.PENDING)
                    .build();

            return MacroExecutionResponseDTO.from(macroExecutionRepository.save(ex));
        } catch (DataIntegrityViolationException dup) {
            // 5) UNIQUE 제약 위반 시 (같은 토큰 중복 사용)
            throw new DomainException(ErrorCode.VALIDATION_FAILED, "토큰이 이미 사용되었습니다.");
        }
    }

    @Async
    @Transactional
    public void runExecution(Long executionId) {
        MacroExecution macroExecution = macroExecutionRepository.findById(executionId)
                .orElseThrow(() -> new DomainException(ErrorCode.RESOURCE_NOT_FOUND, "실행 세션이 소실되었습니다."));

        macroExecution.start(); // status = RUNNING, started_at = now
        macroExecutionRepository.save(macroExecution);

        try {
            // 스텝들 실행
            for (MacroStep step : macroStepRepository.
                    findByMacro_IdOrderByStepOrderAsc(macroExecution.getMacro().getId())) {
                String sourceAccountNo = step.getSourceAccount() == null ? null : step.getSourceAccount().getAccountNo();
                String sourceBankCode = step.getSourceAccount() == null ? null : step.getSourceAccount().getBankCode();
                String targetAccountNo = step.getTargetAccount() == null ? null : step.getTargetAccount().getAccountNo();
                String targetBankCode = step.getTargetAccount() == null ? null : step.getTargetAccount().getBankCode();

                TxnRequestDTO txnRequestDTO = TxnRequestDTO.builder()
                        .txnType(step.getStepType())
                        .currencyCode(step.getCurrencyCode())
                        .amount(step.getAmount())
                        .description(step.getNote())
                        .sourceBankCode(sourceBankCode)
                        .sourceAccountNo(sourceAccountNo)
                        .targetBankCode(targetBankCode)
                        .targetAccountNo(targetAccountNo)
                        .build();

                txnService.createTxn(txnRequestDTO);
            }
            macroExecution.success(); // status = SUCCEEDED, finished_at = now
        } catch (Exception e) {
            macroExecution.fail("MACRO_FAILED", e.getMessage()); // status = FAILED, error_msg 저장
            log.error( e.getMessage());
        }

        macroExecutionRepository.save(macroExecution);
    }

    public MacroExecutionResponseDTO get(Long executionId) {
        return MacroExecutionResponseDTO.from(
                macroExecutionRepository.findById(executionId)
                        .orElseThrow(() -> new DomainException(
                                ErrorCode.RESOURCE_NOT_FOUND,
                                "매크로실행 아이디가 존재하지 않습니다."
                        )));
    }

    public Page<MacroExecutionResponseDTO> getList(
            final Long macroId,
            final ExecutionStatus status,
            final LocalDateTime from,
            final LocalDateTime to,
            final int page,
            final int size
    ) {

        Pageable p = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        LocalDateTime f = (from == null) ? LocalDateTime.MIN : from;
        LocalDateTime t = (to == null) ? LocalDateTime.MAX : to;

        if (status == null) return macroExecutionRepository
                .findByMacro_IdAndStartedAtBetween(macroId, f, t, p)
                .map(MacroExecutionResponseDTO::from);

        return macroExecutionRepository
                .findByMacro_IdAndStatusAndStartedAtBetween(macroId, status, f, t, p)
                .map(MacroExecutionResponseDTO::from);
    }
}
