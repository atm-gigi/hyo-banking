package org.atmgigi.hyobankingbe.macro.controller;

import org.atmgigi.hyobankingbe.macro.entity.MacroQrToken;
import org.atmgigi.hyobankingbe.macro.service.QrTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

@RestController
public class QrTokenController {

    private final QrTokenService service;

    public QrTokenController(QrTokenService service) {
        this.service = service;
    }

    // QR 토큰 생성
    @PostMapping("/macros/{id}/qr-tokens")
    public ResponseEntity<Map<String, Object>> create(@PathVariable Long id) {
        MacroQrToken t = service.create(id, Duration.ofMinutes(5));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("token",t.getToken(),"expires_at",t.getExpiresAt()));
    }

    // QR 토큰 무효화
    @PostMapping("/qr-tokens/{token}/invalidate")
    public ResponseEntity<Void> invalidate(@PathVariable String token) {
        service.invalidate(token);
        return ResponseEntity.noContent().build();
    }

    // QR 토큰 조회
    @GetMapping("/qr-tokens/{token}")
    public ResponseEntity<?> get(@PathVariable String token) {
        return service.find(token)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }




}
