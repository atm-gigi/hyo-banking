package org.atmgigi.hyobankingbe.common.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;

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

    // 리소스 없음: 404 NOT FOUND
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> notFound(NoSuchElementException e) {
        // {"error": "메시지"} 형태로 응답
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }

    // 상태 충돌 (비즈니스 제약 위반 등): 409 CONFLICT
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> conflict(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    }

    // 데이터 무결성 위반(UNIQUE, FK 등)" 409 CONFLICT
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> conflict2(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "DATA_INTEGRITY"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnknown(Exception ex) {
        ProblemDetail pd = ProblemDetail.forStatus(500);
        pd.setTitle("UNKNOWN_ERROR");
        pd.setDetail("Unexpected error" + ex.getMessage());
        return ResponseEntity.status(500).body(pd);
    }
}
