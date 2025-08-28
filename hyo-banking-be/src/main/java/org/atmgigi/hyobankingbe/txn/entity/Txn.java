package org.atmgigi.hyobankingbe.txn.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "txn")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Txn {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //TODO: 연결 필요
    @Column(name = "macro_execution_id")
    private Long macroExecutionId;

    //TODO: 연결 필요
    @Column(name = "user_id",nullable = false)
    private Long userId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String description;
}
