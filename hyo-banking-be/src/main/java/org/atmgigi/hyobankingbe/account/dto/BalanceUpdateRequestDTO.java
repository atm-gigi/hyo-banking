package org.atmgigi.hyobankingbe.account.dto;

import java.math.BigDecimal;

public record BalanceUpdateRequestDTO (
        long userId,
        String account,
        BigDecimal balance
) {}
