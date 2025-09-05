package org.atmgigi.hyobankingbe.macro.entity;

import jakarta.persistence.*;
import lombok.*;
import org.atmgigi.hyobankingbe.macro.domain.StepType;

@Entity
@Table(name = "macro_step")
@Getter
@Setter   // ← 롬복 덕분에 getter/setter 자동 생성
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MacroStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "macro_id", nullable = false)
    private Macro macro; // 어느 매크로에 속하는 단계인지 (FK)

    @Column(nullable = false)
    private Integer stepOrder; // 실행 순서 (1, 2, 3..)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StepType stepType;        // BALANCE_CHECK / WITHDRAW /...

    private Long accountFromId; // 출금 계좌 (nullable)

    private Long accountToId; // 입금 계좌 (nullable)

    private Long amount; // 금액 (nullable)

    private String currencyCode; // 통화 (예: KRW)

    private String note; //비고 설명

}
