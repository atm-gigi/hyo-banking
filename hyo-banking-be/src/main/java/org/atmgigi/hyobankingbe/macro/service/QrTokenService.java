package org.atmgigi.hyobankingbe.macro.service;

import lombok.RequiredArgsConstructor;
import org.atmgigi.hyobankingbe.common.exception.DomainException;
import org.atmgigi.hyobankingbe.common.exception.ErrorCode;
import org.atmgigi.hyobankingbe.macro.dto.MacroResponseDTOs.*;
import org.atmgigi.hyobankingbe.macro.entity.MacroQrToken;
import org.atmgigi.hyobankingbe.macro.repository.MacroQrTokenRepository;
import org.atmgigi.hyobankingbe.macro.repository.MacroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QrTokenService {

    private final MacroQrTokenRepository macroQrTokenRepository;
    private final MacroRepository macroRepository;


    // QR 토큰 생성 (UUID 32자리, 만료시각 설정)
    @Transactional
    public MacroQrTokenResponseDTO create(Long macroId, Duration ttl) {
        MacroQrToken macroQrToken = MacroQrToken.builder()
                .macro(macroRepository.getReferenceById(macroId))                 // // FK: macro_id
                .token(UUID.randomUUID().toString().replace("-", ""))       // // 32자 토큰
                .expiresAt(LocalDateTime.now().plus(ttl))                   // // now + ttl
                .build();
        return MacroQrTokenResponseDTO.from(macroQrTokenRepository.save(macroQrToken));
    }

    // 토큰 사용 / 무효화 (usedAt=now)
    @Transactional
    public void invalidate(String token) {
        MacroQrToken macroQrToken = macroQrTokenRepository.findByToken(token)
                .orElseThrow(() -> new DomainException(ErrorCode.RESOURCE_NOT_FOUND, token + "토큰이 존재하지 않습니다."));
        macroQrToken.setUsedAt(LocalDateTime.now());
    }

    // 토큰 단건 조회
    public MacroQrTokenResponseDTO find(String token) {
        return MacroQrTokenResponseDTO.from(
                macroQrTokenRepository.findByToken(token)
                .orElseThrow(() -> new DomainException(ErrorCode.RESOURCE_NOT_FOUND, token + "토큰이 존재하지 않습니다."))
        );
    }

}
