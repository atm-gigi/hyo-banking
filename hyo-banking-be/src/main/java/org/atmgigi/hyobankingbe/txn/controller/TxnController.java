package org.atmgigi.hyobankingbe.txn.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atmgigi.hyobankingbe.txn.dto.TxnCreatedResponseDTO;
import org.atmgigi.hyobankingbe.txn.dto.TxnRequestDTO;
import org.atmgigi.hyobankingbe.txn.service.TxnService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TxnController {

    private final TxnService txnService;

    @PostMapping
    public ResponseEntity<TxnCreatedResponseDTO> createTxn(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody @Valid TxnRequestDTO txnRequestDTO
    ) {

        //TODO: 유저 주입 및 입출금 판별 후 데이터로 전표 작성
        // 1. 계좌번호로 계좌 엔티티 조회
        // 2. 계좌에서 사용자 연관관계 통해 가져오기
        // 3. 거래 생성 시 user 연결
        // 4. 분개 엔트리 생성

        return ResponseEntity.ok().body(null);
    }
}
