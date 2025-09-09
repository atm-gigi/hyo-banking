package org.atmgigi.hyobankingbe.macro.entity;

import jakarta.persistence.*;
import lombok.*;
import org.atmgigi.hyobankingbe.macro.domain.MacroStatus;
import org.atmgigi.hyobankingbe.user.domain.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "macro")
@Getter
@Setter              // ← getter/setter 자동 생성
@NoArgsConstructor           // ← 기본 생성자
@AllArgsConstructor          // ← 전체 필드 생성자
@Builder                     // ← 빌더 패턴 지원
public class Macro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK, 자동 증가

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; //어떤 사용자의 매크로인지

    @Column(nullable = false)
    private String name; //매크로 이름

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MacroStatus status; // ACTIVE / DRAFT / INACTIVE

    @CreationTimestamp
    private LocalDateTime createdAt; // 생성 시각 자동 기록

    @UpdateTimestamp
    private LocalDateTime updatedAt; //수정 시각 자동 갱신

}
