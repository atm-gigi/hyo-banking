package org.atmgigi.hyobankingbe.macro.entity;

import jakarta.persistence.*;
import lombok.*;
import org.atmgigi.hyobankingbe.macro.domain.ExecutionStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "macro_execution")
@Getter
@Setter   // ← 롬복 덕분에 getter/setter 자동 생성
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MacroExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 어떤 매크로 실행인지
    @JoinColumn(name = "macro_id", nullable = false)
    private Macro macro;

    @OneToOne(fetch = FetchType.LAZY) // 토큰은 1회용 -> 실행과 1:1
    @JoinColumn(name = "qr_token_id" , nullable = false, unique = true)
    private MacroQrToken qrToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionStatus status; // Pending, RUNNING, SUCCEEDED, FAILED

    private String errorMessage;

    private String errorCode;

    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    @Column(nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;

}
