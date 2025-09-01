package org.atmgigi.hyobankingbe.txn.repository;

import org.atmgigi.hyobankingbe.txn.entity.Txn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TxnRepository extends JpaRepository<Txn, Long> {
    @Query("""
        SELECT t FROM Txn t
        LEFT JOIN FETCH t.txnEntries e
        WHERE e.accountId = :accountId
    """)
    Optional<Txn> findDetailTxnByIdAnd(Long txnId);
}
