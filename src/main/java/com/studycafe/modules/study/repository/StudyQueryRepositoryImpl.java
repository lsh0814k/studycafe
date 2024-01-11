package com.studycafe.modules.study.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPQLQuery;
import com.studycafe.modules.study.domain.QStudy;
import com.studycafe.modules.study.domain.QStudyMember;
import com.studycafe.modules.study.domain.Study;
import com.studycafe.modules.tag.domain.QTag;
import com.studycafe.modules.tag.domain.Tag;
import com.studycafe.modules.zone.domain.QZone;
import com.studycafe.modules.zone.domain.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Set;

public class StudyQueryRepositoryImpl extends QuerydslRepositorySupport implements StudyQueryRepository {

    public StudyQueryRepositoryImpl() {
        super(Study.class);
    }

    @Override
    public Page<Study> findByKeyword(String keyword, Pageable pageable) {
        QStudy study = QStudy.study;
        JPQLQuery<Study> query = from(study).where(study.published.isTrue()
                .and(study.title.containsIgnoreCase(keyword))
                .or(study.tags.any().title.containsIgnoreCase(keyword))
                .or(study.zones.any().localNameOfCity.containsIgnoreCase(keyword)))
                .leftJoin(study.tags, QTag.tag).fetchJoin()
                .leftJoin(study.zones, QZone.zone).fetchJoin()
                .leftJoin(study.members, QStudyMember.studyMember).fetchJoin()
                .distinct();

        JPQLQuery<Study> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Study> fetchResults = pageableQuery.fetchResults();
        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
    }

    @Override
    public List<Study> findByMainPage() {
        QStudy study = QStudy.study;
        JPQLQuery<Study> query = from(study).where(study.published.isTrue()
                        .and(study.closed.isFalse()))
                .orderBy(study.publishedDateTime.desc())
                .distinct()
                .limit(9);

        return query.fetch();
    }

    @Override
    public List<Study> findByAccount(Set<Zone> zones, Set<Tag> tags) {
        QStudy study = QStudy.study;
        JPQLQuery<Study> query = from(study).where(study.published.isTrue()
                        .and(study.tags.any().in(tags))
                        .and(study.zones.any().in(zones)))
                .orderBy(study.publishedDateTime.desc())
                .distinct()
                .limit(9);
        return query.fetch();
    }
}
