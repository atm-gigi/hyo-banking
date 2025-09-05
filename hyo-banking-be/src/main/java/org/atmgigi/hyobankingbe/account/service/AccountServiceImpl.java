package org.atmgigi.hyobankingbe.account.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.atmgigi.hyobankingbe.account.domain.Account;
import org.atmgigi.hyobankingbe.account.domain.AccountType;
import org.atmgigi.hyobankingbe.account.dto.AccountCreateRequestDTO;
import org.atmgigi.hyobankingbe.account.dto.AccountInfoResponseDTO;
import org.atmgigi.hyobankingbe.account.dto.BalanceUpdateRequestDTO;
import org.atmgigi.hyobankingbe.account.repository.AccountRepository;
import org.atmgigi.hyobankingbe.user.domain.User;
import org.atmgigi.hyobankingbe.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Override
    public AccountInfoResponseDTO createAccount(AccountCreateRequestDTO dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 사용자입니다."));

        Account account = Account.builder()
                .bankCode(dto.bankCode())
                .accountNo(dto.accountNo())
                .accountType(AccountType.valueOf(dto.accountType()))
                .currencyCode(dto.currencyCode())
                .balanceCache(BigDecimal.valueOf(0))
                .user(user)
                .build();

        accountRepository.save(account);

        return convertDTO(account, dto.userId());
    }

    @Override
    public List<AccountInfoResponseDTO> getUserAccounts(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 사용자입니다."));

        List<Account> accountList = accountRepository.findAllByUser(user);

        return accountList.stream()
                .map(o -> convertDTO(o, userId))
                .collect(Collectors.toList());
    }

    @Override
    public AccountInfoResponseDTO getAccount(long userId, long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 계좌입니다."));

        if(account.getUser().getId() != userId)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 계좌 정보를 조회할 수 없습니다.");


        return convertDTO(account, userId);
    }

    @Override
    @Transactional
    public AccountInfoResponseDTO updateBalance(BalanceUpdateRequestDTO dto) {
        Account account = accountRepository.findByAccountNo(dto.account())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 계좌입니다."));

        if(account.getUser().getId() != dto.userId())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 계좌 정보를 조회할 수 없습니다.");

        account.setBalanceCache(dto.balance());
        accountRepository.save(account);

        return convertDTO(account, dto.userId());
    }

    private AccountInfoResponseDTO convertDTO(Account account, long userId) {
        return AccountInfoResponseDTO.builder()
                .userId(userId)
                .accountId(account.getId())
                .bankCode(account.getBankCode())
                .accountNo(account.getAccountNo())
                .accountType(account.getAccountType().name())
                .currencyCode(account.getCurrencyCode())
                .balanceCache(account.getBalanceCache())
                .build();
    }
}
