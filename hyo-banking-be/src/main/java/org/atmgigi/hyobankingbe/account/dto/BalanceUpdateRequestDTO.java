package org.atmgigi.hyobankingbe.account.dto;

public record BalanceUpdateRequestDTO (
        long userId,
        String account,
        int balance
) {}
