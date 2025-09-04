package org.atmgigi.hyobankingbe.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;

// REST 컨트롤러 전역에서 발생하는 예외를 가로채 처리
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 리소스 없음: 404 NOT FOUND
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> notFound(NoSuchElementException e) {
        // {"error": "메시지"} 형태로 응답
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }

    // 잘못된 요청: 400 BAD REQUEST
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> badRequest(NoSuchElementException e) {
        // {"error": "메시지"} 형태로 응답
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
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

}
