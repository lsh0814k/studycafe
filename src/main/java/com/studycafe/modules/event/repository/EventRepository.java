package com.studycafe.modules.event.repository;

import com.studycafe.modules.event.domain.Event;
import com.studycafe.modules.study.domain.Study;
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

    @Query(value = "select e from Event e " +
            "left join fetch e.study " +
            "where e.id = :id")
    Optional<Event> findWithStudyById(@Param("id") Long id);
}
