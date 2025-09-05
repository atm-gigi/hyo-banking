package org.atmgigi.hyobankingbe.account.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AccountInfoResponseDTO(
        long userId,
        long accountId,
        String bankCode,
        String accountNo,
        String accountType,
        String currencyCode,
        BigDecimal balanceCache
) {
}
