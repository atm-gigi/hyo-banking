package org.atmgigi.hyobankingbe.txn.entity;

import jakarta.persistence.*;
import lombok.*;
import org.atmgigi.hyobankingbe.user.domain.User;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "txn")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Txn {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_txn_user"))
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String description;

    @OneToMany(mappedBy = "txn", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TxnEntry> txnEntries = new ArrayList<>();
}
