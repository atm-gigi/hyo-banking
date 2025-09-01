package org.atmgigi.hyobankingbe.txn.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.atmgigi.hyobankingbe.txn.enums.EntryType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "트랜잭션 조회 분개 DTO")
@Builder
public record TxnEntryDTO(
        Long id,
        Long accountId,
        EntryType entryType,
        BigDecimal amount,
        String currencyCode,
        LocalDateTime createdAt
) {
}
