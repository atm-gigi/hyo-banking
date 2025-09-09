package org.atmgigi.hyobankingbe.macro.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "macro_qr_token")
@Getter
@Setter   // ← 롬복 덕분에 getter/setter 자동 생성
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MacroQrToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "macro_id", nullable = false)
    private Macro macro; // 어떤 매크로의 토큰인지

    @Column(nullable = false, unique = true)
    private String token; // QR 토큰 값 (UUID)

    @Column(nullable = false)
    private LocalDateTime expiresAt; // 만료 시각

    private LocalDateTime usedAt; // 사용된 시각 (한 번 쓰면 채움)

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt; // DB에서 자동 세팅

    @Column(nullable = false, updatable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt; // DB에서 자동 세팅

}
