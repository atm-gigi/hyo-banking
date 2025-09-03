package org.atmgigi.hyobankingbe.txn.repository;

import jakarta.persistence.LockModeType;
import org.atmgigi.hyobankingbe.txn.entity.IdempotencyKey;
import org.atmgigi.hyobankingbe.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, Long> {

    Optional<IdempotencyKey> findByUserAndIdemKey(User user, String idemKey);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from IdempotencyKey i where i.user = :user and i.idemKey = :idemKey")
    Optional<IdempotencyKey> lockByUserAndIdemKey(@Param("user") User user, @Param("idemKey") String idemKey);

    @Modifying
    @Transactional
    @Query("delete from IdempotencyKey i where i.expiresAt < :now")
    int deleteExpired(@Param("now") LocalDateTime now);

}
