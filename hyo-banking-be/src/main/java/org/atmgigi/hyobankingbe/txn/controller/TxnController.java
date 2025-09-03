package org.atmgigi.hyobankingbe.txn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atmgigi.hyobankingbe.txn.dto.TxnCreatedResponseDTO;
import org.atmgigi.hyobankingbe.txn.dto.TxnDetailDTO;
import org.atmgigi.hyobankingbe.txn.dto.TxnRequestDTO;
import org.atmgigi.hyobankingbe.txn.service.IdempotencyHandler;
import org.atmgigi.hyobankingbe.txn.service.TxnService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "트랜잭션 생성 및 조회 API")
public class TxnController {

    private final TxnService txnService;
    private final IdempotencyHandler idempotencyHandler;

    @PostMapping
    @Operation(
            summary = "트랜잭션 생성",
            description = """
            트랜잭션을 생성합니다. 멱등키(Idempotency-Key)를 사용하여 중복 요청을 안전하게 처리합니다.
            성공 시 201 Created와 함께 생성 결과를 반환합니다.
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = TxnCreatedResponseDTO.class),
                            examples = @ExampleObject(name = "created",
                                    value = """
                                          {
                                            "txnId": 42,
                                            "status": "SUCCESS"
                                          }
                                          """))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = org.springframework.http.ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "멱등키 충돌(동일 키로 상이한 본문)",
                    content = @Content(schema = @Schema(implementation = org.springframework.http.ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "검증 실패",
                    content = @Content(schema = @Schema(implementation = org.springframework.http.ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    })
    public ResponseEntity<TxnCreatedResponseDTO> createTxn(
            @Parameter(
                    in = ParameterIn.HEADER,
                    name = "Idempotency-Key",
                    required = true,
                    description = "멱등 요청 식별용 고유 키 (최소 1분~24시간 캐시 권장)",
                    example = "b1c2d3e4-f5a6-47b8-98c7-1234567890ab"
            )
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "트랜잭션 생성 요청 본문",
                    content = @Content(
                            schema = @Schema(implementation = TxnRequestDTO.class),
                            examples = @ExampleObject(name = "deposit",
                                    value = """
                                {
                                  "txnType": "DEPOSIT",
                                  "targetBankCode": "KB",
                                  "targetAccountNo": "110-123-456789",
                                  "amount": 10000,
                                  "currencyCode": "KRW",
                                  "description": "ATM 입금 - 강남지점"
                                }
                                """)
                    )
            )
            @RequestBody @Valid TxnRequestDTO txnRequestDTO
    ) {
        return idempotencyHandler.handle(txnRequestDTO, idempotencyKey);
    }

    @GetMapping
    @Operation(
            summary = "계좌 기준 트랜잭션 목록 조회",
            description = """
            특정 계좌번호에 연관된 트랜잭션을 기간 필터와 함께 조회합니다.
            반환되는 각 트랜잭션에는 모든 분개 엔트리가 포함됩니다.
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TxnDetailDTO.class)))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청(기간 범위 오류 등)",
                    content = @Content(schema = @Schema(implementation = org.springframework.http.ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    })
    public ResponseEntity<List<TxnDetailDTO>> getTxns(
            @Parameter(description = "시작일(포함), ISO-8601", example = "2025-09-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,

            @Parameter(description = "종료일(미포함), ISO-8601", example = "2025-09-05")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,

            @Parameter(description = "계좌번호", required = true, example = "123-456-789")
            @RequestParam(name = "accountNo") String accountNo,

            @Parameter(description = "은행 코드", required = true, example = "KB")
            @RequestParam(name = "bankCode") String bankCode
    ) {
        return ResponseEntity.ok(txnService.getTxns(from, to, accountNo, bankCode));
    }

    @GetMapping("/{txnId}")
    @Operation(
            summary = "트랜잭션 단건 상세 조회",
            description = "트랜잭션 ID로 상세를 조회합니다. 분개 엔트리 목록 포함."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = TxnDetailDTO.class),
                            examples = @ExampleObject(name = "detail",
                                    value = """
                            {
                              "txnId": 42,
                              "username": "changjin",
                              "description": "ATM 입금 - 강남지점",
                              "createdAt": "2025-09-04T14:53:21",
                              "entries": [
                                {
                                  "txnEntryId": 1001,
                                  "accountNo": "SYS-ATM_CASH-KRW",
                                  "bankCode": "KB",
                                  "entryType": "DEBIT",
                                  "amount": -10000,
                                  "currencyCode": "KRW",
                                  "createdAt": "2025-09-04T14:53:21"
                                },
                                {
                                  "txnEntryId": 1002,
                                  "accountNo": "110-123-456789",
                                  "bankCode": "KB",
                                  "entryType": "CREDIT",
                                  "amount": 10000,
                                  "currencyCode": "KRW",
                                  "createdAt": "2025-09-04T14:53:21"
                                }
                              ]
                            }
                            """))),
            @ApiResponse(responseCode = "404", description = "해당 트랜잭션 없음",
                    content = @Content(schema = @Schema(implementation = org.springframework.http.ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    })
    public ResponseEntity<TxnDetailDTO> getTxn(
            @Parameter(in = ParameterIn.PATH, description = "트랜잭션 ID", required = true, example = "42")
            @PathVariable("txnId") Long txnId
    ) {
        return ResponseEntity.ok(txnService.getTxnDetail(txnId));
    }
}
