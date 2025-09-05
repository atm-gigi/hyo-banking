package org.atmgigi.hyobankingbe.macro.entity;

import jakarta.persistence.*;
import lombok.*;
import org.atmgigi.hyobankingbe.common.exception.DomainException;
import org.atmgigi.hyobankingbe.common.exception.ErrorCode;
import org.atmgigi.hyobankingbe.macro.domain.ExecutionStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false, updatable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void start() {
        if (status != ExecutionStatus.PENDING) {
            throw new DomainException(ErrorCode.PROCESSING,"이미 실행 중이거나 완료되었습니다.");
        }
        this.status = ExecutionStatus.RUNNING;
        this.startedAt = LocalDateTime.now();
    }

    public void success() {
        this.status = ExecutionStatus.SUCCEEDED;
        this.finishedAt = LocalDateTime.now();
    }

    public void fail(String errorCode, String errorMessage) {
        this.status = ExecutionStatus.FAILED;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.finishedAt = LocalDateTime.now();
    }

}
