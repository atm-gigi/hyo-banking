package org.atmgigi.hyobankingbe.macro.repository;

import org.atmgigi.hyobankingbe.macro.entity.MacroStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MacroStepRepository extends JpaRepository<MacroStep, Long> {

    // 특정 매크로에 속한 단계들을 순서(stepOrder)대로 조회
    List<MacroStep> findByMacro_IdOrderByStepOrderAsc(Long macroId);

    // 특정 매크로 + 특정 단계 번호(stepOrder)로 단일 단계 조회
    Optional<MacroStep> findByMacro_IdAndStepOrder(Long macroId, Integer stepOrder);

    // 특정 매크로 + 특정 단계 번호(stepOrder)로 삭제
    void deleteByMacro_IdAndStepOrder(Long macroId, Integer stepOrder);

}
