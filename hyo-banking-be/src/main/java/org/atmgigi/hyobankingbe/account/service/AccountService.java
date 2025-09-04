package org.atmgigi.hyobankingbe.account.service;

import org.atmgigi.hyobankingbe.account.dto.AccountCreateRequestDTO;
import org.atmgigi.hyobankingbe.account.dto.AccountInfoResponseDTO;
import org.atmgigi.hyobankingbe.account.dto.BalanceUpdateRequestDTO;

import java.util.List;

public interface AccountService {

    AccountInfoResponseDTO createAccount(AccountCreateRequestDTO dto);

    List<AccountInfoResponseDTO> getUserAccounts(long userId);

    AccountInfoResponseDTO getAccount(long userId, long accountId);

    AccountInfoResponseDTO updateBalance(BalanceUpdateRequestDTO dto);

}
