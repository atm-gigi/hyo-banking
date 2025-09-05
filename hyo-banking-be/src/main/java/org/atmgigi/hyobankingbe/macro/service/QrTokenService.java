package org.atmgigi.hyobankingbe.macro.service;

import org.atmgigi.hyobankingbe.macro.entity.MacroQrToken;
import org.atmgigi.hyobankingbe.macro.repository.MacroQrTokenRepository;
import org.atmgigi.hyobankingbe.macro.repository.MacroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class QrTokenService {

    private final MacroQrTokenRepository tokenRepo;
    private final MacroRepository macroRepo;


    public QrTokenService(MacroQrTokenRepository tokenRepo, MacroRepository macroRepo) {
        this.tokenRepo = tokenRepo;
        this.macroRepo = macroRepo;
    }

    // QR 토큰 생성 (UUID 32자리, 만료시각 설정)
    @Transactional
    public MacroQrToken create(Long macroId, Duration ttl) {
        MacroQrToken t = MacroQrToken.builder()
                .macro(macroRepo.getReferenceById(macroId))                 // // FK: macro_id
                .token(UUID.randomUUID().toString().replace("-", ""))       // // 32자 토큰
                .expiresAt(LocalDateTime.now().plus(ttl))                   // // now + ttl
                .build();
        return tokenRepo.save(t);
    }

    // 토큰 무효화 (usedAt=now)
    @Transactional
    public void invalidate(String token) {
        MacroQrToken t = tokenRepo.findByToken(token)
                .orElseThrow(()-> new IllegalArgumentException("TOKEN_NOT_FOUND"));
        t.setUsedAt(LocalDateTime.now());
    }

    // 토큰 단건 조회
    public Optional<MacroQrToken> find(String token) {
        return tokenRepo.findByToken(token);
    }

}
