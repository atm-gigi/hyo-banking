package org.atmgigi.hyobankingbe.macro.controller;

import lombok.RequiredArgsConstructor;
import org.atmgigi.hyobankingbe.macro.domain.MacroStatus;
import org.atmgigi.hyobankingbe.macro.dto.MacroDtos;
import org.atmgigi.hyobankingbe.macro.entity.Macro;
import org.atmgigi.hyobankingbe.macro.dto.MacroDtos.*;
import org.atmgigi.hyobankingbe.macro.entity.MacroStep;
import org.atmgigi.hyobankingbe.macro.repository.MacroRepository;
import org.atmgigi.hyobankingbe.macro.service.MacroService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 매크로 + 스텝 API
@RestController
@RequestMapping("/macros")
@RequiredArgsConstructor
public class MacroController {

    private final MacroService service;
    private final MacroRepository repo;

    // 매크로 생성
    @PostMapping
    public ResponseEntity<Macro> create(@RequestBody CreateMacroReq req) {
        Macro m = service.create(req.userId(), req.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(m);
    }

    // 매크로 목록 조회
    @GetMapping
    public Page<Macro> list(@RequestParam Long userId,
                            @RequestParam(required = false) MacroStatus status,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "20") int size) {
        Pageable p = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return (status == null)
                ? repo.findByUserId(userId, p)
                : repo.findByUserIdAndStatus(userId, status, p);
    }

    // 매크로 단건 조회
    @GetMapping("/{id}")
    public Macro get(@PathVariable Long id) {
        return service.get(id);
    }

    // 매크로 수정
    @PatchMapping("/{id}")
    public Macro patch(@PathVariable Long id, @RequestBody PatchMacroReq req) {
        return service.patch(id, req.name(), req.status());
    }

    // 매크로 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteIfDraft(id);
        return ResponseEntity.noContent().build();
    }

    // 단계 추가
    @PostMapping("/{id}/steps")
    public ResponseEntity<MacroStep> addStep(@PathVariable Long id, @RequestBody StepReq req) {
        MacroStep s = MacroStep.builder()
                .stepOrder(req.stepOrder())
                .stepType(req.stepType())
                .amount(req.amount()==null?null:req.amount().longValue())
                .currencyCode(req.currencyCode())
                .accountFromId(req.accountFromId())
                .accountToId(req.accountToId())
                .note(req.note())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addStep(id,s));
    }

    // 단계 업서트
    @PutMapping("/{id}/steps/{order}")
    public MacroStep upsertStep(@PathVariable Long id,
                                @PathVariable Integer order,
                                @RequestBody StepReq req) {
        MacroStep s = MacroStep.builder()
                .stepType(req.stepType())
                .amount(req.amount() == null ? null : req.amount().longValue())
                .currencyCode(req.currencyCode())
                .accountFromId(req.accountFromId())
                .accountToId(req.accountToId())
                .note(req.note())
                .build();
        return service.upsertStep(id,order,s);
    }

    // 단계 목록 조회
    @GetMapping("/{id}/steps")
    public List<MacroStep> listSTeps(@PathVariable Long id){
        return service.listSteps(id);
    }

    // 단계 삭제
    @DeleteMapping("/{id}/steps/{order}")
    public ResponseEntity<Void> deleteStep(@PathVariable Long id, @PathVariable Integer order) {
        service.deleteStep(id,order);
        return ResponseEntity.noContent().build();
    }

}
