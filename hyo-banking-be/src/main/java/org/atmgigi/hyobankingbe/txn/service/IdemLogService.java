package org.atmgigi.hyobankingbe.txn.service;

import lombok.RequiredArgsConstructor;
import org.atmgigi.hyobankingbe.common.exception.DomainException;
import org.atmgigi.hyobankingbe.txn.entity.IdempotencyKey;
import org.atmgigi.hyobankingbe.txn.enums.IdemStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IdemLogService {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(final IdempotencyKey row, final DomainException ex, final String bodyJson) {
        row.setStatus(IdemStatus.FAILED);
        row.setErrorCode(ex.getCode().name());
        row.setErrorMsg(ex.getMessage());
        row.setResponseCode(ex.getStatus().value());
        row.setResponseBody(bodyJson);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markSucceeded(final IdempotencyKey row, final int httpCode, final String bodyJson) {
        row.setStatus(IdemStatus.SUCCEEDED);
        row.setResponseCode(httpCode);
        row.setResponseBody(bodyJson);
    }
}
