package org.atmgigi.hyobankingbe.common.exception;

import org.springframework.http.HttpStatus;

public class IdempotencyKeyNotFoundException extends GlobalException {
    public IdempotencyKeyNotFoundException(Long userId, String idemKey) {
        super(
                ErrorCode.IDEM_KEY_NOT_FOUND,
                "멱등키 데이터가 복제 저장 후 사라졌습니다: userId=%d, key=%s"
                        .formatted(userId, idemKey),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
