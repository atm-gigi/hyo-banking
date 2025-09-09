package org.atmgigi.hyobankingbe.account.controller;

import lombok.RequiredArgsConstructor;
import org.atmgigi.hyobankingbe.account.dto.AccountCreateRequestDTO;
import org.atmgigi.hyobankingbe.account.dto.AccountInfoResponseDTO;
import org.atmgigi.hyobankingbe.account.dto.BalanceUpdateRequestDTO;
import org.atmgigi.hyobankingbe.account.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // 계좌 개설
    @PostMapping("/create")
    public ResponseEntity<AccountInfoResponseDTO> createAccount(@RequestBody AccountCreateRequestDTO accountDTO) {
        return ResponseEntity.ok(accountService.createAccount(accountDTO));
    }

    // 내 계좌 조회
    @GetMapping
    public ResponseEntity<List<AccountInfoResponseDTO>> getAccounts(@RequestParam("userId") long userId) {
        return ResponseEntity.ok(accountService.getUserAccounts(userId));
    }

    // 특정 계좌 조회
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountInfoResponseDTO> getAccountBalance(@PathVariable("accountId") long accountId,
                                                                    @RequestParam("userId") long userId ) {
        return ResponseEntity.ok(accountService.getAccount(userId, accountId));
    }

    // 계좌 잔액 변경
    @PutMapping("/balance")
    public ResponseEntity<AccountInfoResponseDTO> updateBalance(@RequestBody BalanceUpdateRequestDTO updateDTO) {
        return ResponseEntity.ok(accountService.updateBalance(updateDTO));
    }

}
