package org.atmgigi.hyobankingbe.txn.repository;

import org.atmgigi.hyobankingbe.txn.entity.Txn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TxnRepository extends JpaRepository<Txn, Long> {
    @Query("""
        select distinct t
        from Txn t
        left join fetch t.txnEntries te
        left join fetch te.account a
        where t.id = :id
    """)
    Optional<Txn> findByIdWithEntries(Long id);


    @Query("""
        select distinct t
        from Txn t
        join t.txnEntries teFilter
        join teFilter.account acc
        left join fetch t.txnEntries teFetch
        left join fetch teFetch.account acc2
        where acc.accountNo = :accountNo and acc.bankCode = :bankCode
        and (:from is null or t.createdAt >= :from)
        and (:to   is null or t.createdAt <  :to)
        order by t.createdAt desc, t.id desc
    """)
    List<Txn> findAllByAccountNoAndCreatedAt(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("accountNo") String accountNo,
            @Param("bankCode") String bankCode
    );
}
