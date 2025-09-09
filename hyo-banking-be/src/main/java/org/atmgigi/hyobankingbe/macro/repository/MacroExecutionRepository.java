package org.atmgigi.hyobankingbe.macro.repository;

import org.atmgigi.hyobankingbe.macro.domain.ExecutionStatus;
import org.atmgigi.hyobankingbe.macro.entity.MacroExecution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

//매크로 실행(MacroExecution) 레포지토리
public interface MacroExecutionRepository extends JpaRepository<MacroExecution, Long> {

    // 특정 매크로 ID 기준 실행 목록
    Page<MacroExecution> findByMacro_Id(Long macroId, Pageable p);

    // 특정 매크로 ID + 상태별 실행 목록
    Page<MacroExecution> findByMacro_IdAndStatus(Long macroId, ExecutionStatus status, Pageable p);

    // 특정 매크로 ID + 상태 + 기간별 실행 목록
    Page<MacroExecution> findByMacro_IdAndStatusAndStartedAtBetween(
            Long macroId, ExecutionStatus status, LocalDateTime from, LocalDateTime to, Pageable p
    );

    // 특정 매크로 ID + 기간별 실행 목록
    Page<MacroExecution> findByMacro_IdAndStartedAtBetween(
            Long macroId, LocalDateTime from, LocalDateTime to, Pageable p
    );

}
