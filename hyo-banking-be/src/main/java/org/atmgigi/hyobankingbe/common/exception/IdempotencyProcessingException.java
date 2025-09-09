package org.atmgigi.hyobankingbe.common.exception;

import org.springframework.http.HttpStatus;

public class IdempotencyProcessingException extends DomainException {
    private final int retryAfterSeconds;
    public IdempotencyProcessingException(String msg, int retryAfterSeconds) {
        super(ErrorCode.PROCESSING, msg, HttpStatus.CONFLICT);
        this.retryAfterSeconds = retryAfterSeconds;
    }
    public int getRetryAfterSeconds() { return retryAfterSeconds; }
}