package org.atmgigi.hyobankingbe.txn.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "트랜잭션 생성 응답 DTO")
public record TxnCreatedResponseDTO(
        @Schema(description = "생성된 트랜잭션 ID", example = "12")
        Long txnId,
        @Schema(description = "트랜잭션 생성 상태", example = "SUCCEEDED")
        String status
){
}
