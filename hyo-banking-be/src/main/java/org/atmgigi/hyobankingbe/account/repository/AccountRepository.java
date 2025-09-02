package org.atmgigi.hyobankingbe.account.repository;

import org.atmgigi.hyobankingbe.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}
