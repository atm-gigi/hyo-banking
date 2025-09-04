package org.atmgigi.hyobankingbe.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.atmgigi.hyobankingbe.user.domain.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByLoginId(String loginId);
    boolean existsByPassword(String password);

    Optional<User> findByLoginIdAndPassword(String loginId, String password);

    Optional<User> findByLoginId(String loginId);

    Optional<User> findById(long id);

}
