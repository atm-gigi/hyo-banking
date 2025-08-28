package org.atmgigi.hyobankingbe.txn.entity;

import jakarta.persistence.*;
import lombok.*;
import org.atmgigi.hyobankingbe.txn.enums.EntryType;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "txn_entry",
        indexes = @Index(name = "idx_entry_acct_time", columnList = "account_id, created_at")
)
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class TxnEntry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK: txn_id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "txn_id", nullable = false, foreignKey = @ForeignKey(name = "fk_entry_txn"))
    private Txn txn;

    // TODO: 연결 필요
    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "entry_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EntryType entryType;

    @Column(nullable = false, precision = 18, scale = 0)
    private BigDecimal amount;

    // ISO 4217 코드 3자리
    @Column(name = "currency_code", nullable = false, length = 3)
    @Builder.Default
    private String currencyCode = "KRW";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
