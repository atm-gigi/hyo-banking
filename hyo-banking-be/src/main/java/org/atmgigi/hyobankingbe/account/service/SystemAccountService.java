package org.atmgigi.hyobankingbe.account.service;

import lombok.RequiredArgsConstructor;
import org.atmgigi.hyobankingbe.account.domain.Account;
import org.atmgigi.hyobankingbe.account.domain.AccountType;
import org.atmgigi.hyobankingbe.account.repository.AccountRepository;
import org.atmgigi.hyobankingbe.common.exception.DomainException;
import org.atmgigi.hyobankingbe.common.exception.ErrorCode;
import org.atmgigi.hyobankingbe.user.domain.User;
import org.atmgigi.hyobankingbe.user.service.SystemUserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SystemAccountService {
    private final AccountRepository accountRepository;
    private final SystemUserService systemUserService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Account getOrCreateSystem(AccountType type, String currencyCode) {
        return accountRepository.findByAccountTypeAndCurrencyCode(type, currencyCode)
                .orElseGet(() -> {
                    try {
                        User user = systemUserService.getOrCreateSystem();
                        Account a = Account.builder()
                                .accountType(type)
                                .user(user)
                                .currencyCode(currencyCode)
                                .accountNo("SYS-%s-%s".formatted(type.name().substring(0,3), currencyCode))
                                .build();
                        return accountRepository.save(a);
                    } catch (DataIntegrityViolationException e) {
                        return accountRepository.findByAccountTypeAndCurrencyCode(type, currencyCode)
                                .orElseThrow(() -> new DomainException(
                                        ErrorCode.ACCOUNT_NOT_FOUND, "시스템 계정이 소실되었습니다."));
                    }
                });
    }
}
