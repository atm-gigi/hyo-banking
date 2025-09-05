package org.atmgigi.hyobankingbe.macro.service;

import org.atmgigi.hyobankingbe.macro.domain.ExecutionStatus;
import org.atmgigi.hyobankingbe.macro.entity.MacroExecution;
import org.atmgigi.hyobankingbe.macro.entity.MacroQrToken;
import org.atmgigi.hyobankingbe.macro.repository.MacroExecutionRepository;
import org.atmgigi.hyobankingbe.macro.repository.MacroQrTokenRepository;
import org.atmgigi.hyobankingbe.macro.repository.MacroRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ExecutionService {

    private final MacroExecutionRepository execRepo;
    private final MacroQrTokenRepository tokenRepo;
    private final MacroRepository macroRepo;

    public ExecutionService(MacroExecutionRepository execRepo, MacroQrTokenRepository tokenRepo, MacroRepository macroRepo) {
        this.execRepo = execRepo;
        this.tokenRepo = tokenRepo;
        this.macroRepo = macroRepo;
    }

    // 실행 세션 시작 (토큰 1회성 보장)
    @Transactional
    public MacroExecution start(Long macroId, String qrTokenString) {
        // 1) (macro_id, token)으로 토큰 조회
        MacroQrToken token = tokenRepo.findByMacro_IdAndToken(macroId,qrTokenString)
                .orElseThrow(()-> new IllegalArgumentException("TOKEN_NOT_FOUND"));

        // 2) 유효성 (만료/사용됨) 체크
        LocalDateTime now = LocalDateTime.now();
        if (token.getUsedAt() != null || token.getExpiresAt().isBefore(now)){
            throw new IllegalStateException("TOKEN_EXPIRED_OR_USED");
        }

        // 3) 사용 처리 (재사용 방지)
        token.setUsedAt(now);

        try {
            // 4) 실행 엔티티 생성 (토큰과 매크로는 엔티티로 연결)
            MacroExecution ex = MacroExecution.builder()
                    .macro(macroRepo.getReferenceById(macroId)) // // FK: macro_id
                    .qrToken(token) // FK: qr_token_id (unique)
                    .status(ExecutionStatus.PENDING)
                    .build();

            return execRepo.save(ex);
        } catch (DataIntegrityViolationException dup) {
            // 5) UNIQUE 제약 위반 시 (같은 토큰 중복 사용)
            throw new IllegalStateException("TOKEN_ALREADY_USED");
        }
    }

}
