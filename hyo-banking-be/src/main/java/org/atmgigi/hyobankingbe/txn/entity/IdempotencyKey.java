package org.atmgigi.hyobankingbe.txn.entity;

import jakarta.persistence.*;
import lombok.*;
import org.atmgigi.hyobankingbe.txn.enums.OperationType;
import org.atmgigi.hyobankingbe.txn.enums.IdemStatus;
import org.atmgigi.hyobankingbe.user.domain.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "idempotency_key",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_key",
                columnNames = {"user_id", "idem_key"}
        ),
        indexes = @Index(name = "idx_idem_expires", columnList = "expires_at")
)
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
public class IdempotencyKey {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_idem_user"))
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OperationType operation;

    @Column(name = "idem_key", nullable = false, length = 64)
    private String idemKey;

    @Column(name = "request_hash" ,nullable = false, length = 64)
    private String requestHash;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private IdemStatus status;

    @Column(name = "response_code", nullable = false)
    private int responseCode;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response_body", columnDefinition = "json")
    private String responseBody;

    @Column(name = "error_code", length = 50)
    private String errorCode;

    @Column(name = "error_msg")
    private String errorMsg;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
}
