package org.atmgigi.hyobankingbe.txn.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "트랜잭션 조회 응답 DTO")
@Builder
public record TxnDetailDTO(
        @Schema(description = "트랜잭션 ID", example = "42")
        Long txnId,
        @Schema(description = "거래 소유자(사용자) 이름", example = "고창진")
        String username,
        @Schema(description = "거래 설명", example = "ATM 입금 - 강남지점")
        String description,
        @Schema(description = "트랜잭션 생성 시각", example = "2025-09-04T14:53:21", format = "date-time", accessMode = Schema.AccessMode.READ_ONLY)
        LocalDateTime createdAt,
        @ArraySchema(
                arraySchema = @Schema(description = "분개 엔트리 목록(2개)"),
                minItems = 2,
                uniqueItems = false,
                schema = @Schema(implementation = TxnEntryDTO.class)
        )
        List<TxnEntryDTO> entries
) {
}
