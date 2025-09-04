package org.atmgigi.hyobankingbe.macro.dto;

import org.atmgigi.hyobankingbe.macro.domain.MacroStatus;
import org.atmgigi.hyobankingbe.macro.domain.StepType;

// 매크로 생성 요청 바디용 DTO들을 감싸는 클래스 (내부에 record 타입들을 정의)
public class MacroDtos {

    // 매크로 생성 요청 바디용 DTO: { "userId": 1, "name": "월말 이체" }
    public record CreateMacroReq(Long userId, String name) {}

    // 매크로 수정(PATCH) 요청 바디용 DTO: { "name": "...", "status" : "ACTIVE" }
    public record PatchMacroReq(String name, MacroStatus status) {}

    // 스텝 생성/수정 요청
    public record StepReq(
            Integer stepOrder, // 실행 순서 (POST에서만 사용)
            StepType stepType, // BALANCE_CHECK / WITHDRAW / DEPOSIT / TRANSFER
            Long amount, // 금액 (nullable)
            String currencyCode, // 통화 (예: KRW)
            Long accountFromId, // 출금 계좌 (nullable)
            Long accountToId, // 입금 계좌 (nullable)
            String note // 비고 (nullable)
    ) {}

}


