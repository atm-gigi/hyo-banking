package org.atmgigi.hyobankingbe.txn.service;

import lombok.RequiredArgsConstructor;
import org.atmgigi.hyobankingbe.account.domain.Account;
import org.atmgigi.hyobankingbe.account.domain.AccountType;
import org.atmgigi.hyobankingbe.account.repository.AccountRepository;
import org.atmgigi.hyobankingbe.common.exception.DomainException;
import org.atmgigi.hyobankingbe.common.exception.ErrorCode;
import org.atmgigi.hyobankingbe.txn.dto.TxnCreatedResponseDTO;
import org.atmgigi.hyobankingbe.txn.dto.TxnDetailDTO;
import org.atmgigi.hyobankingbe.txn.dto.TxnEntryDTO;
import org.atmgigi.hyobankingbe.txn.dto.TxnRequestDTO;
import org.atmgigi.hyobankingbe.txn.entity.Txn;
import org.atmgigi.hyobankingbe.txn.entity.TxnEntry;
import org.atmgigi.hyobankingbe.txn.enums.EntryType;
import org.atmgigi.hyobankingbe.txn.repository.TxnRepository;
import org.atmgigi.hyobankingbe.user.domain.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TxnServiceImpl implements TxnService {

    private final TxnRepository txnRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TxnCreatedResponseDTO createTxn(final TxnRequestDTO txnRequestDTO) {

        Account debit;   // amount 음수로 들어갈 계좌
        Account credit;  // amount 양수로 들어갈 계좌
        User actor;

        switch (txnRequestDTO.txnType()) {
            case DEPOSIT -> {
                Account target = findAccountOrThrow(txnRequestDTO.targetBankCode(), txnRequestDTO.targetAccountNo());
                Account cash   = getOrCreateSystem(AccountType.ATM_CASH, txnRequestDTO.currencyCode());
                debit  = cash;
                credit = target;
                actor  = target.getUser();
            }
            case WITHDRAW -> {
                Account source = findAccountOrThrow(txnRequestDTO.sourceBankCode(), txnRequestDTO.sourceAccountNo());
                Account cash   = getOrCreateSystem(AccountType.ATM_CASH, txnRequestDTO.currencyCode());
                debit  = source;
                credit = cash;
                actor  = source.getUser();
            }
            case TRANSFER -> {
                // 무통장입금 : 내부정산 계정 -> 받는이
                Account target = findAccountOrThrow(txnRequestDTO.targetBankCode(), txnRequestDTO.targetAccountNo());
                Account internal = getOrCreateSystem(AccountType.BANK_INTERNAL, txnRequestDTO.currencyCode());
                debit  = internal;
                credit = target;
                actor  = target.getUser();
            }
            default -> throw new DomainException(ErrorCode.VALIDATION_FAILED, "지원하지 않는 거래 유형입니다.");
        }

        Txn saved = saveTxnWithEntries(actor, debit, credit,
                txnRequestDTO.amount(), txnRequestDTO.currencyCode(), txnRequestDTO.description());

        return TxnCreatedResponseDTO.builder()
                .txnId(saved.getId())
                .status("SUCCESS")
                .build();
    }

    @Override
    public List<TxnDetailDTO> getTxns(LocalDate from, LocalDate to, String accountNo, String bankCode) {
        if (to != null && from != null && to.isBefore(from)) {
            throw new DomainException(ErrorCode.VALIDATION_FAILED, "`to`는 `from`보다 이후여야 합니다.");
        }
        LocalDateTime fromDt = (from != null) ? from.atStartOfDay() : null;
        LocalDateTime toDt   = (to   != null) ? to.plusDays(1).atStartOfDay() : null;

        List<Txn> txns = txnRepository.findAllByAccountNoAndCreatedAt(fromDt, toDt, accountNo, bankCode);

        return txns.stream().map(this::toTxnDetailDTO).toList();
    }

    @Override
    public TxnDetailDTO getTxnDetail(final Long txnId) {
        Txn txn = txnRepository.findByIdWithEntries(txnId)
                .orElseThrow(() -> new DomainException(ErrorCode.RESOURCE_NOT_FOUND, "거래가 없습니다."));

        return toTxnDetailDTO(txn);
    }

    private TxnDetailDTO toTxnDetailDTO(Txn txn) {

        List<TxnEntryDTO> entryDTO = txn.getTxnEntries().stream()
                .map(e -> TxnEntryDTO.builder()
                        .txnEntryId(e.getId())
                        .entryType(e.getEntryType())
                        .amount(e.getAmount())
                        .currencyCode(e.getCurrencyCode())
                        .bankCode(e.getAccount().getBankCode())
                        .accountNo(e.getAccount().getAccountNo())
                        .createdAt(e.getCreatedAt())
                        .build()).toList();

        return TxnDetailDTO.builder()
                .txnId(txn.getId())
                .entries(entryDTO)
                .username(txn.getUser().getName())
                .description(txn.getDescription())
                .createdAt(txn.getCreatedAt())
                .build();
    }

    private Account findAccountOrThrow(String bankCode, String accountNo) {
        return accountRepository.findByAccountNoAndBankCode(accountNo, bankCode)
                .orElseThrow(() -> new DomainException(
                        ErrorCode.ACCOUNT_NOT_FOUND,
                        bankCode + " 은행에 계좌번호가" + accountNo + "인 계좌가 없습니다."));
    }

    private Txn saveTxnWithEntries(User actor,
                                   Account debitAcc,
                                   Account creditAcc,
                                   BigDecimal amount,
                                   String currencyCode,
                                   String description) {

        Txn txn = Txn.builder()
                .user(actor)
                .description(description)
                .build();

        addEntry(txn, debitAcc,  EntryType.DEBIT,  amount.negate(), currencyCode);
        addEntry(txn, creditAcc, EntryType.CREDIT, amount,          currencyCode);

        ensureBalanced(txn);
        return txnRepository.save(txn);
    }

    private void addEntry(Txn txn, Account account, EntryType type, BigDecimal amt, String ccy) {
        if (account == null) {
            throw new DomainException(ErrorCode.VALIDATION_FAILED, "필요한 계좌가 지정되지 않았습니다.");
        }
        TxnEntry e = TxnEntry.builder()
                .txn(txn)
                .account(account)
                .entryType(type)
                .amount(amt)
                .currencyCode(ccy)
                .build();
        txn.getTxnEntries().add(e);
    }

    private void ensureBalanced(Txn txn) {
        BigDecimal sum = txn.getTxnEntries().stream()
                .map(TxnEntry::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (sum.compareTo(BigDecimal.ZERO) != 0) {
            throw new DomainException(ErrorCode.VALIDATION_FAILED, "분개 합계가 0이 아닙니다.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Account getOrCreateSystem(AccountType type, String currencyCode) {
        return accountRepository.findByAccountTypeAndCurrencyCode(type, currencyCode)
                .orElseGet(() -> {
                    try {
                        Account a = Account.builder()
                                .accountType(type)
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
