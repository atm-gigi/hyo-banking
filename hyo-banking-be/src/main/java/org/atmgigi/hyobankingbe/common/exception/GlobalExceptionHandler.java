package org.atmgigi.hyobankingbe.common.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IdempotencyProcessingException.class)
    public ResponseEntity<ProblemDetail> handleIdempotencyProcessingException(IdempotencyProcessingException ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.RETRY_AFTER, String.valueOf(ex.getRetryAfterSeconds()));
        return ResponseEntity.status(ex.getStatus().value()).headers(headers)
                .body(ErrorResponses.of(ex));
    }

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ProblemDetail> handleException(GlobalException ex) {
        return ResponseEntity.status(ex.getStatus().value())
                .body(ErrorResponses.of(ex));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnknown(Exception ex) {
        ProblemDetail pd = ProblemDetail.forStatus(500);
        pd.setTitle("UNKNOWN_ERROR");
        pd.setDetail("Unexpected error");
        return ResponseEntity.status(500).body(pd);
    }
}
