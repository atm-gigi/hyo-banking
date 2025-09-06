package org.atmgigi.hyobankingbe.macro.controller;

import lombok.RequiredArgsConstructor;
import org.atmgigi.hyobankingbe.macro.domain.ExecutionStatus;
import org.atmgigi.hyobankingbe.macro.dto.ExecutionDTOs.*;
import org.atmgigi.hyobankingbe.macro.dto.MacroResponseDTOs.*;
import org.atmgigi.hyobankingbe.macro.service.ExecutionService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/executions")
@RequiredArgsConstructor
public class ExecutionController {

    private final ExecutionService executionService;

    // 실행 시작
    @PostMapping
    public ResponseEntity<MacroExecutionResponseDTO> start(@RequestBody StartExecutionRequestDTO startExecutionRequestDTO) {
        MacroExecutionResponseDTO ex = executionService.start(
                startExecutionRequestDTO.macroId(),
                startExecutionRequestDTO.qrToken());

        executionService.runExecution(startExecutionRequestDTO.macroId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ex);
    }

    // 실행 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<MacroExecutionResponseDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(
                    executionService.get(id)
        );

    }

    // 실행 목록 조회
    @GetMapping
    public ResponseEntity<Page<MacroExecutionResponseDTO>> list(@RequestParam Long macroId,
                                     @RequestParam(required = false) ExecutionStatus status,
                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(
                executionService.getList(macroId, status, from, to, page, size)
        );

    }

}
