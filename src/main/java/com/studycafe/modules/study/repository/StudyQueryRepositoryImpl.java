package com.studycafe.modules.study.repository;

import com.querydsl.jpa.JPQLQuery;
import com.studycafe.modules.study.domain.QStudy;
import com.studycafe.modules.study.domain.QStudyMember;
import com.studycafe.modules.study.domain.Study;
import com.studycafe.modules.tag.domain.QTag;
import com.studycafe.modules.zone.domain.QZone;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class StudyQueryRepositoryImpl extends QuerydslRepositorySupport implements StudyQueryRepository {

    public StudyQueryRepositoryImpl() {
        super(Study.class);
    }

    @Override
    public List<Study> findByKeyword(String keyword) {
        QStudy study = QStudy.study;
        JPQLQuery<Study> query = from(study).where(study.published.isTrue()
                .and(study.title.containsIgnoreCase(keyword))
                .or(study.tags.any().title.containsIgnoreCase(keyword))
                .or(study.zones.any().localNameOfCity.containsIgnoreCase(keyword)))
                .leftJoin(study.tags, QTag.tag).fetchJoin()
                .leftJoin(study.zones, QZone.zone).fetchJoin()
                .leftJoin(study.members, QStudyMember.studyMember).fetchJoin()
                .distinct();


        return query.fetch();
    }
}
