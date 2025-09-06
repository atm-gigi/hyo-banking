package org.atmgigi.hyobankingbe.account.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.atmgigi.hyobankingbe.user.domain.User;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "account",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"bankCode", "accountNo"}),
                @UniqueConstraint(columnNames = {"accoundNo", "currencyCode"})
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String bankCode;

    @Column(nullable = false)
    private String accountNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;

    @Column(nullable = false)
    private String currencyCode;

    @Column(nullable = false)
    private BigDecimal balanceCache;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void setBalanceCache(BigDecimal balance) {
        this.balanceCache = this.balanceCache.add(balance);
    }

    public void deposit(BigDecimal balance) {
        this.balanceCache = this.balanceCache.add(balance);
    }

    public void withdraw(BigDecimal balance) {
        this.balanceCache = this.balanceCache.subtract(balance);
    }
}
