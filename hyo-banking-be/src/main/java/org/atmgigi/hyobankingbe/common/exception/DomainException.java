package org.atmgigi.hyobankingbe.common.exception;

import org.springframework.http.HttpStatus;

public class DomainException extends GlobalException {

    public DomainException(ErrorCode code, String message) {
        this(code, message, HttpStatus.BAD_REQUEST);
    }

    public DomainException(ErrorCode code, String message, HttpStatus status) {
        super(code, message, status);
    }
}
