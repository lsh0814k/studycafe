package com.studycafe.modules.study.repository;

import com.studycafe.modules.study.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long>, StudyQueryRepository {
    boolean existsByPath(String path);

    @Query(value = "select s from Study s " +
            "left join fetch s.tags " +
            "left join fetch s.zones " +
            "left join fetch s.managers ma " +
            "left join fetch ma.account maa " +
            "left join fetch s.members me " +
            "left join fetch me.account mea " +
            "where s.path = :path")
    Optional<Study> findAllByPath(@Param("path") String path);

    Optional<Study> findByPath(String path);

    @Query(value = "select s from Study s " +
            "left join fetch s.tags " +
            "left join fetch s.managers m " +
            "left join fetch m.account " +
            "where s.path = :path")
    Optional<Study> findStudyWithTagsByPath(@Param("path") String path);

    @Query(value = "select s from Study s " +
            "left join fetch s.zones " +
            "left join fetch s.managers m " +
            "left join fetch m.account " +
            "where s.path = :path")
    Optional<Study> findStudyWithZonesByPath(@Param("path") String path);

    @Query(value = "select s from Study s " +
            "left join fetch s.members m " +
            "left join fetch m.account " +
            "where s.path = :path")
    Optional<Study> getStudyToUpdateMember(@Param("path") String path);

    @Query(value = "select s from Study s " +
            "left join fetch s.managers m " +
            "left join fetch m.account " +
            "where s.path = :path")
    Optional<Study> findStudyWithManagerByPath(@Param("path") String path);


    @Query(value = "select s from Study s " +
            "left join fetch s.tags " +
            "left join fetch s.zones " +
            "where s.id = :id")
    Optional<Study> findStudyWithTagsAndZonesById(@Param("id") Long id);


    @Query(value = "select s from Study s " +
            "left join fetch s.managers ma " +
            "left join fetch ma.account " +
            "left join fetch s.members me " +
            "left join fetch me.account " +
            "where s.id = :id")
    Optional<Study> findStudyWithManagersAndMembersById(@Param("id") Long id);

    @Query(value = "select s from Study s " +
            "join fetch s.managers m " +
            "join fetch m.account a " +
            "where a.id = :id")
    List<Study> findFirst5WithManagerByAccountId(@Param("id") Long id);

    @Query(value = "select s from Study s " +
            "join fetch s.members m " +
            "join fetch m.account a " +
            "where a.id = :id")
    List<Study> findFirst5WithMemberByAccountId(@Param("id") Long id);
}
