package com.studcafe.study.repository;

import com.studcafe.study.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long> {
    boolean existsByPath(String path);

    @Query(value = "select s from Study s " +
            "left join fetch s.tags " +
            "left join fetch s.zones " +
            "left join fetch s.managers ma " +
            "left join fetch ma.account maa " +
            "left join fetch s.members me " +
            "left join fetch me.account mea")
    Optional<Study> findAllByPath(String path);

    Optional<Study> findByPath(String path);
}
