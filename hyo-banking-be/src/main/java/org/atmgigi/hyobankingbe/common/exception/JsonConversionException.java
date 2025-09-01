package org.atmgigi.hyobankingbe.common.exception;

import org.springframework.http.HttpStatus;

public class JsonConversionException extends GlobalException {
    public JsonConversionException(String message) {
        super(ErrorCode.DOWNSTREAM_ERROR, message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
