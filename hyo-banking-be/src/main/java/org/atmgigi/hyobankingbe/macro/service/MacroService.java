package org.atmgigi.hyobankingbe.macro.service;

import org.atmgigi.hyobankingbe.macro.domain.MacroStatus;
import org.atmgigi.hyobankingbe.macro.domain.StepType;
import org.atmgigi.hyobankingbe.macro.entity.Macro;
import org.atmgigi.hyobankingbe.macro.entity.MacroStep;
import org.atmgigi.hyobankingbe.macro.repository.MacroRepository;
import org.atmgigi.hyobankingbe.macro.repository.MacroStepRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

// 매크로와 매크로 단계(MacroStep) 관련 비즈니스 로직 처리
@Service
public class MacroService {
    private final MacroRepository macroRepo;
    private final MacroStepRepository stepRepo;

    public MacroService(MacroRepository macroRepo, MacroStepRepository stepRepo) {
        this.macroRepo = macroRepo;
        this.stepRepo = stepRepo;
    }

    // 매크로 생성 (userId, name 받아서 상태는 기본 ACTIVE)
    @Transactional
    public Macro create(Long userId, String name) {
        Macro m = Macro.builder()
                .userId(userId)
                .name(name)
                .status(MacroStatus.ACTIVE)
                .build();
        return macroRepo.save(m);
    }

    // 매크로 단건 조회 (없으면 예외 발생)
    public Macro get(Long id) {
        return macroRepo.findById(id)
                .orElseThrow(()-> new NoSuchElementException("MACRO_NOT_FOUND"));
    }

    // 매크로 수정 (name, status 일부만 업데이트 가능)
    @Transactional
    public Macro patch(Long id, String name, MacroStatus status) {
        Macro m = get(id);
        if (name != null) m.setName(name);
        if (status != null) m.setStatus(status);
        return macroRepo.save(m);
    }

    // 매크로 삭제 (DRAFT 상태일 때만 가능)
    @Transactional
    public void deleteIfDraft(Long id) {
        Macro m = get(id);
        if (m.getStatus() != MacroStatus.DRAFT) {
            throw new IllegalStateException("ONLY_DRAFT_DELETABLE");
        }
        macroRepo.deleteById(id);
    }

    // 단계 유효성 검증 (StepType별로 필드 조건 확인)
    private void validateStep(MacroStep s) {
        StepType t = s.getStepType();
        switch (t) {
            case BALANCE_CHECK -> {
                if (s.getAmount() != null) throw new IllegalArgumentException("BALANCE_CHECK.amount must be null");
                if (s.getAccountFromId() == null) throw new IllegalArgumentException("BALANCE_CHECK.from required");
                if (s.getAccountToId() != null) throw new IllegalArgumentException("BALANCE_CHECK.to must be null");
            }
            case WITHDRAW -> {
                if (s.getAccountFromId() == null) throw new IllegalArgumentException("WITHDRAW.from required");
                if (s.getAccountToId() != null) throw new IllegalArgumentException("WITHDRAW.to must be null");
                if (s.getAmount() == null || s.getCurrencyCode() == null)
                    throw new IllegalArgumentException("WITHDRAW amount/currency required");
            }
            case DEPOSIT -> {
                if (s.getAccountToId() == null) throw new IllegalArgumentException("DEPOSIT.to required");
                if (s.getAccountFromId() != null) throw new IllegalArgumentException("DEPOSIT.from must be null");
                if (s.getAmount() == null || s.getCurrencyCode() == null)
                    throw new IllegalArgumentException("DEPOSIT amount/currency required");
            }
            case TRANSFER -> {
                if (s.getAccountFromId() == null || s.getAccountToId() == null)
                    throw new IllegalArgumentException("TRANSFER from/to required");
                if (s.getAmount() == null || s.getCurrencyCode() == null)
                    throw new IllegalArgumentException("TRANSFER amount/currency required");
            }
        }
    }

    // 단계 추가 (POST)
    @Transactional
    public MacroStep addStep(Long macroId, MacroStep s) {
        s.setMacro(macroRepo.getReferenceById(macroId));  // // FK: macro_id
        validateStep(s);
        return stepRepo.save(s);
    }

    // 단계 업서트 (있으면 수정, 없으면 시규)
    @Transactional
    public MacroStep upsertStep(Long macroId,  Integer order, MacroStep s) {
        s.setMacro(macroRepo.getReferenceById(macroId)); // FK: macro_id
        s.setStepOrder(order);
        validateStep(s);

        return stepRepo.findByMacro_IdAndStepOrder(macroId,order)
                .map(old -> {
                    s.setId(old.getId());
                    return stepRepo.save(s); //기존 행 업데이트
                })
                .orElseGet(()-> stepRepo.save(s)); // 없으면 신규 생성
    }

    // 단계 목록 조회 (step_order ASC)
    public List<MacroStep> listSteps(Long macroId) {
        return stepRepo.findByMacro_IdOrderByStepOrderAsc(macroId);
    }

    // 단계 삭제 (특정 매크로의 특정 순서)
    @Transactional
    public void deleteStep(Long macroId, Integer order) {
        stepRepo.deleteByMacro_IdAndStepOrder(macroId,order);
    }


}
