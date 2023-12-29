package com.studycafe.modules.account.repository;

import com.studycafe.modules.account.domain.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long>, QuerydslPredicateExecutor<Account> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<Account> findByEmail(String email);

    Optional<Account> findByNickname(String nickname);

    @EntityGraph(attributePaths = {"tags"})
    Optional<Account> findWithTagsByEmail(String email);

    @EntityGraph(attributePaths = {"zones"})
    Optional<Account> findWithZonesByEmail(String email);
}
