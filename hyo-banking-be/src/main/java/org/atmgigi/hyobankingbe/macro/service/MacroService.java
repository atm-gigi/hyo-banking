package org.atmgigi.hyobankingbe.macro.service;

import lombok.RequiredArgsConstructor;
import org.atmgigi.hyobankingbe.account.domain.Account;
import org.atmgigi.hyobankingbe.account.repository.AccountRepository;
import org.atmgigi.hyobankingbe.common.exception.DomainException;
import org.atmgigi.hyobankingbe.common.exception.ErrorCode;
import org.atmgigi.hyobankingbe.macro.domain.MacroStatus;
import org.atmgigi.hyobankingbe.macro.dto.MacroDTOs;
import org.atmgigi.hyobankingbe.macro.dto.MacroResponseDTOs.*;
import org.atmgigi.hyobankingbe.macro.entity.Macro;
import org.atmgigi.hyobankingbe.macro.entity.MacroStep;
import org.atmgigi.hyobankingbe.macro.repository.MacroRepository;
import org.atmgigi.hyobankingbe.macro.repository.MacroStepRepository;
import org.atmgigi.hyobankingbe.user.domain.User;
import org.atmgigi.hyobankingbe.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 매크로와 매크로 단계(MacroStep) 관련 비즈니스 로직 처리
@Service
@RequiredArgsConstructor
public class MacroService {
    private final MacroRepository macroRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final MacroStepRepository macroStepRepository;


    // 매크로 생성 (userId, name 받아서 상태는 기본 DRAFT)
    @Transactional
    public MacroResponseDTO create(Long userId, String name) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND, "유저가 존재하지 않습니다."));

        Macro m = Macro.builder()
                .user(user)
                .name(name)
                .status(MacroStatus.DRAFT)
                .build();

        return MacroResponseDTO.from(macroRepository.save(m));
    }

    // 매크로 단건 조회 (없으면 예외 발생)
    public MacroResponseDTO get(Long id) {
        return MacroResponseDTO.from(
                macroRepository.findById(id)
                        .orElseThrow(() -> new DomainException(ErrorCode.RESOURCE_NOT_FOUND, "매크로가 존재하지 않습니다."))
        );
    }

    public Page<MacroResponseDTO> getList(Long userId, MacroStatus status, Pageable pageable) {
        return (status == null)
                ? macroRepository.findByUser_Id(userId, pageable).map(MacroResponseDTO::from)
                : macroRepository.findByUser_IdAndStatus(userId, status, pageable).map(MacroResponseDTO::from);
    }

    // 매크로 수정 (name, status 일부만 업데이트 가능)
    @Transactional
    public MacroResponseDTO patch(Long id, String name, MacroStatus status) {
        Macro macro = macroRepository.findById(id)
                .orElseThrow(() -> new DomainException(ErrorCode.RESOURCE_NOT_FOUND, "매크로가 존재하지 않습니다."));
        if (name != null) macro.setName(name);
        if (status != null) macro.setStatus(status);
        return MacroResponseDTO.from(macroRepository.save(macro));
    }

    // 매크로 삭제 (DRAFT 상태일 때만 가능)
    @Transactional
    public void deleteIfDraft(Long id) {
        Macro m = macroRepository.findById(id)
                .orElseThrow(() -> new DomainException(ErrorCode.RESOURCE_NOT_FOUND, "매크로가 존재하지 않습니다."));
        if (m.getStatus() != MacroStatus.DRAFT) {
            throw new IllegalStateException("ONLY_DRAFT_DELETABLE");
        }
        macroRepository.deleteById(id);
    }

    // 단계 유효성 검증 (StepType별로 필드 조건 확인)
//    private void validateStep(MacroStep macroStep) {
//        OperationType t = macroStep.getStepType();
//        switch (t) {
//            case BALANCE_CHECK -> {
//                if (macroStep.getAmount() != null) throw new IllegalArgumentException("BALANCE_CHECK.amount must be null");
//                if (macroStep.getSourceAccount() == null) throw new IllegalArgumentException("BALANCE_CHECK.from required");
//                if (macroStep.getTargetAccount() != null) throw new IllegalArgumentException("BALANCE_CHECK.to must be null");
//            }
//            case WITHDRAW -> {
//                if (macroStep.getSourceAccount() == null) throw new IllegalArgumentException("WITHDRAW.from required");
//                if (macroStep.getTargetAccount() != null) throw new IllegalArgumentException("WITHDRAW.to must be null");
//                if (macroStep.getAmount() == null || macroStep.getCurrencyCode() == null)
//                    throw new IllegalArgumentException("WITHDRAW amount/currency required");
//            }
//            case DEPOSIT -> {
//                if (macroStep.getTargetAccount() == null) throw new IllegalArgumentException("DEPOSIT.to required");
//                if (macroStep.getSourceAccount() != null) throw new IllegalArgumentException("DEPOSIT.from must be null");
//                if (macroStep.getAmount() == null || macroStep.getCurrencyCode() == null)
//                    throw new IllegalArgumentException("DEPOSIT amount/currency required");
//            }
//            case TRANSFER -> {
//                if (macroStep.getSourceAccount() == null || macroStep.getTargetAccount() == null)
//                    throw new IllegalArgumentException("TRANSFER from/to required");
//                if (macroStep.getAmount() == null || macroStep.getCurrencyCode() == null)
//                    throw new IllegalArgumentException("TRANSFER amount/currency required");
//            }
//        }
//    }

    // 단계 추가 (POST)
    @Transactional
    public MacroStepResponseDTO addStep(Long macroId, MacroDTOs.StepRequestDTO stepRequestDTO) {
        Account sourceAccount = accountRepository.findByAccountNoAndBankCode(
                stepRequestDTO.sourceAccountNo(),
                stepRequestDTO.sourceBankCode()
        ).orElse(null);

        Account targetAccount = accountRepository.findByAccountNoAndBankCode(
                stepRequestDTO.targetAccountNo(),
                stepRequestDTO.targetBankCode()
        ).orElse(null);

        MacroStep s = MacroStep.builder()
                .stepOrder(stepRequestDTO.stepOrder())
                .stepType(stepRequestDTO.stepType())
                .amount(stepRequestDTO.amount())
                .currencyCode(stepRequestDTO.currencyCode())
                .sourceAccount(sourceAccount)
                .targetAccount(targetAccount)
                .note(stepRequestDTO.note())
                .build();

        s.setMacro(macroRepository.getReferenceById(macroId));  // // FK: macro_id
//        validateStep(s);
        return MacroStepResponseDTO.from(macroStepRepository.save(s));
    }

    // 단계 업서트 (있으면 수정, 없으면 시규)
    @Transactional
    public MacroStepResponseDTO upsertStep(Long macroId, int order, MacroDTOs.StepRequestDTO stepRequestDTO) {
        Account sourceAccount = accountRepository.findByAccountNoAndBankCode(
                stepRequestDTO.sourceAccountNo(),
                stepRequestDTO.sourceBankCode()
        ).orElse(null);

        Account targetAccount = accountRepository.findByAccountNoAndBankCode(
                stepRequestDTO.targetAccountNo(),
                stepRequestDTO.targetBankCode()
        ).orElse(null);

        MacroStep s = MacroStep.builder()
                .stepType(stepRequestDTO.stepType())
                .amount(stepRequestDTO.amount())
                .currencyCode(stepRequestDTO.currencyCode())
                .sourceAccount(sourceAccount)
                .targetAccount(targetAccount)
                .note(stepRequestDTO.note())
                .build();

        s.setMacro(macroRepository.getReferenceById(macroId)); // FK: macro_id
        s.setStepOrder(order);
//        validateStep(s);

        return MacroStepResponseDTO.from(
                macroStepRepository.findByMacro_IdAndStepOrder(macroId,order)
                .map(old -> {
                    s.setId(old.getId());
                    return macroStepRepository.save(s); //기존 행 업데이트
                })
                .orElseGet(()-> macroStepRepository.save(s))
        ); // 없으면 신규 생성
    }

    // 단계 목록 조회 (step_order ASC)
    public List<MacroStepResponseDTO> getStepList(Long macroId) {
        return macroStepRepository.findByMacro_IdOrderByStepOrderAsc(macroId)
                .stream()
                .map(MacroStepResponseDTO::from)
                .toList();
    }

    // 단계 삭제 (특정 매크로의 특정 순서)
    @Transactional
    public void deleteStep(Long macroId, Integer order) {
        macroStepRepository.deleteByMacro_IdAndStepOrder(macroId,order);
    }


}
