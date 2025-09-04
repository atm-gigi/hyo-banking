package org.atmgigi.hyobankingbe.txn.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "트랜잭션 생성 응답 DTO")
@Builder
public record TxnCreatedResponseDTO(
        @Schema(description = "생성된 트랜잭션 ID", example = "12")
        Long txnId,
        @Schema(description = "트랜잭션 생성 상태", example = "SUCCEEDED")
        String status
){
}
