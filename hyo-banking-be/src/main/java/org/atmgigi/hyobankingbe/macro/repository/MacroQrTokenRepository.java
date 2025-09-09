package org.atmgigi.hyobankingbe.macro.repository;

import org.atmgigi.hyobankingbe.macro.entity.MacroQrToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//매크로 QR 토근(MacroQrToken) 레포지토리
public interface MacroQrTokenRepository extends JpaRepository<MacroQrToken, Long> {

    // 토큰 문자열 값으로 조회
    Optional<MacroQrToken> findByToken(String token);

    // 특정 매크로 ID + 토큰 문자열로 조회
    Optional<MacroQrToken> findByMacro_IdAndToken(Long macroId, String token);

}
