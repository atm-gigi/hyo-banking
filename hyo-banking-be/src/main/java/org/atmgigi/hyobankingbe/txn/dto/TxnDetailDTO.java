package org.atmgigi.hyobankingbe.txn.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "트랜잭션 조회 응답 DTO")
@Builder
public record TxnDetailDTO(
        Long txnId,
        Long userId,
        String description,
        LocalDateTime createdAt,
        List<TxnEntryDTO> entries
) {
}
