package org.atmgigi.hyobankingbe.txn.service;

import lombok.RequiredArgsConstructor;
import org.atmgigi.hyobankingbe.common.exception.DomainException;
import org.atmgigi.hyobankingbe.txn.entity.IdempotencyKey;
import org.atmgigi.hyobankingbe.txn.enums.IdemStatus;
import org.atmgigi.hyobankingbe.txn.repository.IdempotencyKeyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IdemLogService {
    private final IdempotencyKeyRepository repo;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(final IdempotencyKey row, final DomainException ex, final String bodyJson) {
        repo.save(row.toBuilder()
                .status(IdemStatus.FAILED)
                .errorCode(ex.getCode().name())
                .errorMsg(ex.getMessage())
                .responseCode(ex.getStatus().value())
                .responseBody(bodyJson)
                .build());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markSucceeded(final IdempotencyKey row, final int httpCode, final String bodyJson) {
        repo.save(row.toBuilder()
                .status(IdemStatus.SUCCEEDED)
                .responseCode(httpCode)
                .responseBody(bodyJson)
                .build());
    }
}
