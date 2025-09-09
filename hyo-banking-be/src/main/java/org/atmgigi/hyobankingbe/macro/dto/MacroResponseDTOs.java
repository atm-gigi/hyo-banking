package org.atmgigi.hyobankingbe.macro.dto;

import org.atmgigi.hyobankingbe.macro.domain.ExecutionStatus;
import org.atmgigi.hyobankingbe.macro.domain.MacroStatus;
import org.atmgigi.hyobankingbe.macro.entity.Macro;
import org.atmgigi.hyobankingbe.macro.entity.MacroExecution;
import org.atmgigi.hyobankingbe.macro.entity.MacroQrToken;
import org.atmgigi.hyobankingbe.macro.entity.MacroStep;
import org.atmgigi.hyobankingbe.txn.enums.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MacroResponseDTOs {
    public record MacroQrTokenResponseDTO(
            Long id,
            Long macroId,
            String token,
            LocalDateTime expiresAt,
            LocalDateTime usedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public static MacroQrTokenResponseDTO from(MacroQrToken token) {
            return new MacroQrTokenResponseDTO(
                    token.getId(),
                    token.getMacro() != null ? token.getMacro().getId() : null,
                    token.getToken(),
                    token.getExpiresAt(),
                    token.getUsedAt(),
                    token.getCreatedAt(),
                    token.getUpdatedAt()
            );
        }
    }

    public record MacroResponseDTO(
            Long id,
            String name,
            MacroStatus status,
            Long userId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public static MacroResponseDTO from(Macro macro) {
            return new MacroResponseDTO(
                    macro.getId(),
                    macro.getName(),
                    macro.getStatus(),
                    macro.getUser().getId(),
                    macro.getCreatedAt(),
                    macro.getUpdatedAt()
            );
        }
    }

    public record MacroStepResponseDTO(
            Long id,
            Long macroId,
            int stepOrder,
            OperationType stepType,
            String sourceAccountNo,
            String sourceBankCode,
            String targetAccountNo,
            String targetBankCode,
            BigDecimal amount,
            String currencyCode,
            String note
    ) {
        public static MacroStepResponseDTO from(MacroStep step) {
            return new MacroStepResponseDTO(
                    step.getId(),
                    step.getMacro().getId(),
                    step.getStepOrder(),
                    step.getStepType(),
                    step.getSourceAccount() != null ? step.getSourceAccount().getAccountNo() : null,
                    step.getSourceAccount() != null ? step.getSourceAccount().getBankCode() : null,
                    step.getTargetAccount() != null ? step.getTargetAccount().getAccountNo() : null,
                    step.getTargetAccount() != null ? step.getTargetAccount().getBankCode() : null,
                    step.getAmount(),
                    step.getCurrencyCode(),
                    step.getNote()
            );
        }
    }

    public record MacroExecutionResponseDTO(
            Long id,
            Long macroId,
            Long qrTokenId,
            ExecutionStatus status,
            String errorCode,
            String errorMessage,
            LocalDateTime startedAt,
            LocalDateTime finishedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public static MacroExecutionResponseDTO from(MacroExecution execution) {
            return new MacroExecutionResponseDTO(
                    execution.getId(),
                    execution.getMacro() != null ? execution.getMacro().getId() : null,
                    execution.getQrToken() != null ? execution.getQrToken().getId() : null,
                    execution.getStatus(),
                    execution.getErrorCode(),
                    execution.getErrorMessage(),
                    execution.getStartedAt(),
                    execution.getFinishedAt(),
                    execution.getCreatedAt(),
                    execution.getUpdatedAt()
            );
        }
    }
}
