package org.atmgigi.hyobankingbe.txn.repository;

import org.atmgigi.hyobankingbe.txn.entity.Txn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TxnRepository extends JpaRepository<Txn, Long> {
}
