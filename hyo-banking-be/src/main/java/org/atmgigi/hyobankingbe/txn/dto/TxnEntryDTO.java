package org.atmgigi.hyobankingbe.txn.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.atmgigi.hyobankingbe.txn.enums.EntryType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "트랜잭션 조회 분개 DTO")
@Builder
public record TxnEntryDTO(
        @Schema(description = "분개 엔트리 ID", example = "987654321")
        Long txnEntryId,
        @Schema(description = "계좌번호", example = "110-123-456")
        String accountNo,
        @Schema(description = "은행 코드", example = "KB")
        String bankCode,
        @Schema(description = "엔트리 유형 (DEBIT=차변/출금, CREDIT=대변/입금)",
                example = "DEBIT",
                allowableValues = {"DEBIT", "CREDIT"})
        EntryType entryType,
        @Schema(description = "금액 (출금은 음수, 입금은 양수)", example = "10000")
        BigDecimal amount,
        @Schema(description = "통화 코드(ISO 4217, 3자리)", example = "KRW", minLength = 3, maxLength = 3)
        String currencyCode,
        @Schema(description = "분개 생성 시각", example = "2025-09-04T14:53:21", format = "date-time", accessMode = Schema.AccessMode.READ_ONLY)
        LocalDateTime createdAt
) {
}
