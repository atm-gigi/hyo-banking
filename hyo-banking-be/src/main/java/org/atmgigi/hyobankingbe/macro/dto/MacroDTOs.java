package org.atmgigi.hyobankingbe.macro.dto;

import jakarta.validation.constraints.NotNull;
import org.atmgigi.hyobankingbe.macro.domain.MacroStatus;
import org.atmgigi.hyobankingbe.txn.enums.OperationType;

import java.math.BigDecimal;

// 매크로 생성 요청 바디용 DTO들을 감싸는 클래스 (내부에 record 타입들을 정의)
public class MacroDTOs {

    // 매크로 생성 요청 바디용 DTO: { "userId": 1, "name": "월말 이체" }
    public record CreateMacroRequestDTO(Long userId, String name) {}

    // 매크로 수정(PATCH) 요청 바디용 DTO: { "name": "...", "status" : "ACTIVE" }
    public record PatchMacroRequestDTO(String name, MacroStatus status) {}

    // 스텝 생성/수정 요청
    public record StepRequestDTO(
            Integer stepOrder, // 실행 순서 (POST에서만 사용)
            OperationType stepType, // BALANCE_CHECK / WITHDRAW / DEPOSIT / TRANSFER
            @NotNull
            BigDecimal amount, // 금액
            String currencyCode, // 통화 (예: KRW)
            String sourceBankCode,
            String sourceAccountNo, // 출금 계좌 (nullable)
            String targetBankCode,
            String targetAccountNo, // 입금 계좌 (nullable)
            String note // 비고 (nullable)
    ) {}

}


