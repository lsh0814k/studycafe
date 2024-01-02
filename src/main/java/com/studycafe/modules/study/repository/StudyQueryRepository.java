package com.studycafe.modules.study.repository;

import com.studycafe.modules.study.domain.Study;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;


@Transactional(readOnly = true)
public interface StudyQueryRepository {
    Page<Study> findByKeyword(String keyword, Pageable pageable);
}
