package com.studycafe.modules.study.repository;

import com.studycafe.modules.study.domain.Study;
import com.studycafe.modules.tag.domain.Tag;
import com.studycafe.modules.zone.domain.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;


@Transactional(readOnly = true)
public interface StudyQueryRepository {
    Page<Study> findByKeyword(String keyword, Pageable pageable);

    List<Study> findByMainPage();

    List<Study> findByAccount(Set<Zone> zones, Set<Tag> tags);
}
