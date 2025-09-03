package org.atmgigi.hyobankingbe.txn.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.atmgigi.hyobankingbe.common.exception.*;
import org.atmgigi.hyobankingbe.common.util.DigestUtil;
import org.atmgigi.hyobankingbe.common.util.JsonUtil;
import org.atmgigi.hyobankingbe.txn.dto.TxnCreatedResponseDTO;
import org.atmgigi.hyobankingbe.txn.dto.TxnRequestDTO;
import org.atmgigi.hyobankingbe.txn.entity.IdempotencyKey;
import org.atmgigi.hyobankingbe.txn.enums.IdemStatus;
import org.atmgigi.hyobankingbe.txn.repository.IdempotencyKeyRepository;
import org.atmgigi.hyobankingbe.txn.repository.TxnEntryRepository;
import org.atmgigi.hyobankingbe.txn.repository.TxnRepository;
import org.atmgigi.hyobankingbe.user.domain.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class IdempotencyHandler {

    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final TxnService txnService;
    private final IdemLogService idemLogService;

    private final ObjectMapper objectMapper;

    @Transactional
    public ResponseEntity<TxnCreatedResponseDTO> handle(final TxnRequestDTO txnRequestDTO, final String idempotencyKey) {

        //0. 계좌 정보로 유저 찾기
        User user = null;

        //1. 멱등키 저장 및 확인
        final String requestJSON = JsonUtil.toCanonicalJson(objectMapper, txnRequestDTO);
        final String requestHash = DigestUtil.toSha256Hex(requestJSON);

        IdempotencyKey data = IdempotencyKey.builder()
                .idemKey(idempotencyKey)
                .user(user)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .operation(txnRequestDTO.txnType())
                .status(IdemStatus.PROCESSING)
                .requestHash(requestHash)
                .responseCode(0)
                .build();

        try {
            data = idempotencyKeyRepository.saveAndFlush(data);
        }catch (DataIntegrityViolationException ex) { // 유니크 키 그런 것들로 인해 실패했을때 ( 이미 키값이 존재함 )
            // 2. 중복된 정보 가져오기
            IdempotencyKey existing = idempotencyKeyRepository.lockByUserAndIdemKey(user, idempotencyKey)
                    .orElseThrow(() -> new IdempotencyKeyNotFoundException(user.getId(), idempotencyKey));

            if(!Objects.equals(existing.getRequestHash(), requestHash)) {
                throw new IdempotencyConflictException("멱등키 충돌");
            }

            if(existing.getStatus() == IdemStatus.SUCCEEDED) {
                // 이미 처리 완료된 작업이면 똑같은 응답
                TxnCreatedResponseDTO result = JsonUtil.toObject(objectMapper, existing.getResponseBody(), TxnCreatedResponseDTO.class);
                return ResponseEntity.status(existing.getResponseCode())
                        .body(result);
            }

            if (existing.getStatus() == IdemStatus.PROCESSING) {
                // 409 + Retry-After 헤더로 응답
                throw new IdempotencyProcessingException("거래 트랜잭션 동작중", 2);
            }

            // FAILED인 경우
            throw new IdempotencyFailedPreviouslyException("트랜잭션은 이미 실패했습니다.");
        }

        // 3. 실제 비즈니스 처리
        try {
            TxnCreatedResponseDTO result = txnService.createTxn(txnRequestDTO); // 계좌 처리 등 도메인 로직

            idemLogService.markSucceeded(data, 201, JsonUtil.toCanonicalJson(objectMapper,result));

            return ResponseEntity.status(201).body(result);

        } catch (DomainException ex) {

            idemLogService.markFailed(data, ex, JsonUtil.toCanonicalJson(objectMapper,ex));

            throw ex;
        }

    }

    private static String nullToEmpty(String s) { return s == null ? "" : s; }
}
