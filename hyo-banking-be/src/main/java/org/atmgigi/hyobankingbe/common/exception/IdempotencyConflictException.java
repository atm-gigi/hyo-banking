package org.atmgigi.hyobankingbe.common.exception;

import org.springframework.http.HttpStatus;

public class IdempotencyConflictException extends DomainException {
    public IdempotencyConflictException(String msg) {
        super(ErrorCode.IDEM_CONFLICT, msg, HttpStatus.CONFLICT);
    }
}
