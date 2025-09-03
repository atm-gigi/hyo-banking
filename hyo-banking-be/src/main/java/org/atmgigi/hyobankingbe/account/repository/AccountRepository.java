package org.atmgigi.hyobankingbe.account.repository;

import org.atmgigi.hyobankingbe.account.domain.Account;
import org.atmgigi.hyobankingbe.account.domain.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNoAndBankCode(String accountNo, String bankCode);

    Optional<Account> findByAccountTypeAndCurrencyCode(AccountType type, String currencyCode);
}
