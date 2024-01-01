package com.studycafe.modules.study.repository;

import com.studycafe.modules.study.domain.Study;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface StudyQueryRepository {
    List<Study> findByKeyword(String keyword);
}
