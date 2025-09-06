package org.atmgigi.hyobankingbe.macro.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atmgigi.hyobankingbe.macro.domain.MacroStatus;
import org.atmgigi.hyobankingbe.macro.dto.MacroResponseDTOs.*;
import org.atmgigi.hyobankingbe.macro.dto.MacroDTOs.*;
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
@RequestMapping("/api/macros")
@RequiredArgsConstructor
public class MacroController {

    private final MacroService macroService;


    // 매크로 목록 조회
    @GetMapping
    public ResponseEntity<Page<MacroResponseDTO>> list(@RequestParam Long userId,
                                                                        @RequestParam(required = false) MacroStatus status,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "20") int size) {
        Pageable p = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return ResponseEntity.ok(macroService.getList(userId, status, p));
    }

    // 매크로 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<MacroResponseDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(macroService.get(id));
    }

    // 매크로 생성
    @PostMapping
    public ResponseEntity<MacroResponseDTO> create(@RequestBody CreateMacroRequestDTO createMacroRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                macroService.create(createMacroRequestDTO.userId(), createMacroRequestDTO.name())
        );
    }

    // 매크로 수정
    @PatchMapping("/{id}")
    public MacroResponseDTO patch(@PathVariable Long id, @RequestBody PatchMacroRequestDTO patchMacroRequestDTO) {
        return macroService.patch(id, patchMacroRequestDTO.name(), patchMacroRequestDTO.status());
    }

    // 매크로 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        macroService.deleteIfDraft(id);
        return ResponseEntity.noContent().build();
    }

    // 단계 추가
    @PostMapping("/{id}/steps")
    public ResponseEntity<MacroStepResponseDTO> addStep(@PathVariable Long id, @RequestBody @Valid StepRequestDTO req) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(macroService.addStep(id,req));
    }

    // 단계 업서트
    @PutMapping("/{id}/steps/{order}")
    public ResponseEntity<MacroStepResponseDTO> upsertStep(@PathVariable Long id,
                                @PathVariable int order,
                                @RequestBody StepRequestDTO req) {

        return ResponseEntity.ok(macroService.upsertStep(id, order, req));
    }

    // 단계 목록 조회
    @GetMapping("/{id}/steps")
    public List<MacroStepResponseDTO> getMacroSteps(@PathVariable Long id){
        return macroService.getStepList(id);
    }

    // 단계 삭제
    @DeleteMapping("/{id}/steps/{order}")
    public ResponseEntity<Void> deleteStep(@PathVariable Long id, @PathVariable int order) {
        macroService.deleteStep(id,order);
        return ResponseEntity.noContent().build();
    }

}
