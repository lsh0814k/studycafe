package com.studcafe.event.repository;

import com.studcafe.event.domain.Event;
import com.studcafe.study.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query(value = "select e from Event e " +
            "left join fetch e.createdBy " +
            "left join fetch e.enrollments en " +
            "left join fetch en.account " +
            "where e.id = :id")
    Optional<Event> findWithEnrollmentById(@Param("id") Long id);


    @Query(value = "select e from Event e " +
            "left join fetch e.enrollments em " +
            "where e.study = :study " +
            "order by e.startDateTime")
    List<Event> findByStudyOrderByStartDateTime(@Param("study") Study study);
}