package org.atmgigi.hyobankingbe.macro.controller;

import lombok.RequiredArgsConstructor;
import org.atmgigi.hyobankingbe.macro.dto.MacroResponseDTOs.*;
import org.atmgigi.hyobankingbe.macro.service.QrTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
public class QrTokenController {

    private final QrTokenService QrTokenService;

    // QR 토큰 생성
    @PostMapping("/api/macros/{id}/qr-tokens")
    public ResponseEntity<MacroQrTokenResponseDTO> create(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(QrTokenService.create(id, Duration.ofMinutes(5)));
    }

    // QR 토큰 무효화
    @PostMapping("/api/qr-tokens/{token}/invalidate")
    public ResponseEntity<Void> invalidate(@PathVariable String token) {
        QrTokenService.invalidate(token);
        return ResponseEntity.noContent().build();
    }

    // QR 토큰 조회
    @GetMapping("/api/qr-tokens/{token}")
    public ResponseEntity<MacroQrTokenResponseDTO> get(@PathVariable String token) {
        return ResponseEntity.ok(QrTokenService.find(token));
    }




}
