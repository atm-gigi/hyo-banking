package org.atmgigi.hyobankingbe.common.exception;

import org.springframework.http.HttpStatus;

public class IdempotencyFailedPreviouslyException extends DomainException {
    public IdempotencyFailedPreviouslyException(String message) {
        super(ErrorCode.IDEM_FAILED_PREVIOUSLY, message, HttpStatus.CONFLICT);
    }
}
