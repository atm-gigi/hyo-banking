package org.atmgigi.hyobankingbe.txn.repository;

import org.atmgigi.hyobankingbe.txn.entity.TxnEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TxnEntryRepository extends JpaRepository<TxnEntry, Long> {
}
