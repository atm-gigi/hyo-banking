package org.atmgigi.hyobankingbe.macro.controller;

import org.atmgigi.hyobankingbe.macro.domain.ExecutionStatus;
import org.atmgigi.hyobankingbe.macro.domain.MacroStatus;
import org.atmgigi.hyobankingbe.macro.dto.ExecutionDtos;
import org.atmgigi.hyobankingbe.macro.entity.MacroExecution;
import org.atmgigi.hyobankingbe.macro.repository.MacroExecutionRepository;
import org.atmgigi.hyobankingbe.macro.service.ExecutionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/executions")
public class ExecutionController {

    private final ExecutionService service;
    private final MacroExecutionRepository repo;

    public ExecutionController(ExecutionService service, MacroExecutionRepository repo) {
        this.service = service;
        this.repo = repo;
    }

    // 실행 시작
    @PostMapping
    public ResponseEntity<?> start(@RequestBody ExecutionDtos.StartExecReq req) {
        MacroExecution ex = service.start(req.macro_id(), req.qr_token());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id", ex.getId(), "status", ex.getStatus()));
    }

    // 실행 단건 조회
    @GetMapping("/{id}")
    public MacroExecution get(@PathVariable Long id) {
        return repo.findById(id).orElseThrow();
    }

    // 실행 목록 조회
    @GetMapping
    public Page<MacroExecution> list(@RequestParam Long macroId,
                                     @RequestParam(required = false) ExecutionStatus status,
                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "20") int size) {
        Pageable p = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        LocalDateTime f = (from == null) ? LocalDateTime.MIN : from;
        LocalDateTime t = (to == null) ? LocalDateTime.MAX : to;
        if (status == null) return repo.findByMacro_IdAndStartedAtBetween(macroId, f, t, p);
        return repo.findByMacro_IdAndStatusAndStartedAtBetween(macroId, status, f, t, p);
    }

}
