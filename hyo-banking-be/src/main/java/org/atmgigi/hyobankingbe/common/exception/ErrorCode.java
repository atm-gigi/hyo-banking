package org.atmgigi.hyobankingbe.common.exception;

public enum ErrorCode {
    // --- Idempotency ---
    IDEM_CONFLICT,          // 동일 키+다른 페이로드(409)
    PROCESSING,             // 처리중(409) + Retry-After
    IDEM_FAILED_PREVIOUSLY, // 과거 실패 이력(409)
    IDEM_KEY_NOT_FOUND,     // 중복처리 경로에서 키 조회 실패(500 권장)
    IDEM_EXPIRED,           // TTL 만료(409 또는 410)

    // --- Validation / Common client errors ---
    VALIDATION_FAILED,      // 요청 필드 유효성 실패(400)
    INVALID_STATE,          // 현재 상태에서 수행 불가(409/422)
    RESOURCE_NOT_FOUND,     // 일반 리소스 없음(404)

    // --- Auth / Access ---
    UNAUTHENTICATED,        // 인증 필요(401)
    FORBIDDEN,              // 권한 없음(403)
    RATE_LIMITED,           // 과도한 요청(429)

    // --- Business (금융 도메인) ---
    USER_NOT_FOUND,         // 유저 없음(404)
    INSUFFICIENT_FUNDS,     // 잔액 부족(422/400)
    DAILY_LIMIT_EXCEEDED,   // 1일 한도 초과(422)
    TRANSFER_LIMIT_EXCEEDED,// 거래 한도 초과(422)
    ACCOUNT_NOT_FOUND,      // 계좌 없음(404)
    ACCOUNT_LOCKED,         // 계좌 동결(423/403)
    ACCOUNT_CLOSED,         // 해지/폐쇄 계좌(409/422)
    BENEFICIARY_NOT_FOUND,  // 수취인 정보 없음(404)
    CURRENCY_MISMATCH,      // 통화 불일치(409/422)
    CUT_OFF_TIME_PASSED,    // 마감 시간 경과(409/422)
    DUPLICATE_TRANSACTION,  // 비즈니스 중복 감지(409)

    // --- Infra / External ---
    DOWNSTREAM_TIMEOUT,     // 외부 연동 타임아웃(504)
    DOWNSTREAM_UNAVAILABLE, // 외부 시스템 불가(503)
    DOWNSTREAM_ERROR,       // 외부 일반 오류(502)
    DB_ERROR,               // DB 오류(500)

    // --- Fallback ---
    UNKNOWN_ERROR           // 기타(500)
}
