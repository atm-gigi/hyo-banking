package org.atmgigi.hyobankingbe.txn.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.atmgigi.hyobankingbe.txn.enums.OperationType;

import java.math.BigDecimal;

@Schema(description = "트랜잭션(전표) 등록 DTO")
public record TxnRequestDTO (
        @Schema(description = "트랜잭션 타입", example = "DEPOSIT")
        @NotNull
        OperationType txnType,

        @Schema(description = "출금 계좌 은행코드", example = "KB")
        String sourceBankCode,

        @Schema(description = "출금 계좌" , example = "xxx-xxx-xxx")
        String sourceAccountNo,

        @Schema(description = "입금 계좌 은행코드", example = "KB")
        String targetBankCode,

        @Schema(description = "입금 계좌", example = "xxx-xxx-xxx")
        String targetAccountNo,

        @Schema(description = "금액", example = "10000")
        @NotNull @Positive
        @Digits(integer = 18, fraction = 0)
        BigDecimal amount,

        @Schema(description = "통화코드", example = "KRW")
        @NotBlank @Size(min = 3, max = 3)
        String currencyCode,

        @Schema(description = "메모", example = "ATM 출금 - 강남지점")
        @NotBlank @Size(max = 255)
        String description
) {
    public TxnRequestDTO {
        if (currencyCode == null) {
            currencyCode = "KRW";
        }
    }
}
