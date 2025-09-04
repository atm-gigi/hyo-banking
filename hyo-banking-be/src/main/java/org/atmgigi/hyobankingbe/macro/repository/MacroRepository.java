package org.atmgigi.hyobankingbe.macro.repository;

import org.atmgigi.hyobankingbe.macro.domain.MacroStatus;
import org.atmgigi.hyobankingbe.macro.entity.Macro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

// 매크로 (Macro) 기본 CRUD + 조회용 레포지토리
public interface MacroRepository extends JpaRepository<Macro, Long> {

    // 특정 userId로 매크로 목록 조회 (페이징 지원)
    Page<Macro> findByUserId(Long userId, Pageable p);

    // 특정 userId + 상태 (MacroStatus)로 매크로 목록 조회 (페이징 지원)
    Page<Macro> findByUserIdAndStatus(Long userId, MacroStatus status, Pageable p);
}
