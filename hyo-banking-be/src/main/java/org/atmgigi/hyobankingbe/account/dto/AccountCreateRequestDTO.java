package org.atmgigi.hyobankingbe.account.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.atmgigi.hyobankingbe.account.domain.AccountType;

public record AccountCreateRequestDTO(
        long userId,
        String bankCode,
        String accountNo,
        String accountType,
        String currencyCode
) {
}
