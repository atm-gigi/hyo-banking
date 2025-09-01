package org.atmgigi.hyobankingbe.common.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class GlobalException extends RuntimeException {
    private final ErrorCode code;
    private final HttpStatus status;
    private final Map<String, Object> meta;

    public GlobalException(ErrorCode code, String msg, HttpStatus status) {
        super(msg);
        this.code = code;
        this.status = status;
        this.meta = null;
    }

    public ErrorCode getCode()     { return code; }
    public HttpStatus getStatus()  { return status; }
    public Map<String, Object> getMeta()  { return meta; }

}
