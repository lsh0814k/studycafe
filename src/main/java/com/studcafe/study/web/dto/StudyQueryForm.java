package com.studcafe.study.web.dto;

import com.studcafe.account.domain.Account;
import com.studcafe.study.domain.Study;
import com.studcafe.study.domain.StudyManager;
import com.studcafe.study.domain.StudyMember;
import com.studcafe.tag.dto.TagsQueryForm;
import com.studcafe.zone.dto.ZonesQueryForm;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

import static lombok.AccessLevel.*;

@Getter @Builder @AllArgsConstructor(access = PRIVATE)
public class StudyQueryForm {
    private Set<ZonesQueryForm> zones;
    private Set<TagsQueryForm> tags;
    private String path;
    private String title;
    private String shortDescription;
    private String fullDescription;
    private Boolean published;
    private Boolean closed;
    private Boolean recruiting;
    private Boolean useBanner;
    private Boolean isManager;
    private Boolean isJoinable;
    private Boolean isMember;


    public static StudyQueryForm createForm(Study study, Account account) {
        return StudyQueryForm
                .builder()
                .zones(study.getZones().stream()
                    .map(z -> ZonesQueryForm.builder()
                        .id(z.getId())
                        .localNameOfCity(z.getLocalNameOfCity())
                        .build())
                    .collect(Collectors.toSet())
                )
                .tags(study.getTags().stream()
                        .map(t -> TagsQueryForm.builder()
                                .title(t.getTitle())
                                .build()
                        )
                        .collect(Collectors.toSet())
                )
                .path(study.getPath())
                .title(study.getTitle())
                .shortDescription(study.getShortDescription())
                .fullDescription(study.getFullDescription())
                .published(study.isPublished())
                .closed(study.isClosed())
                .recruiting(study.isRecruiting())
                .recruiting(study.isUseBanner())
                .isJoinable(getisJoinable(study, account))
                .isMember(getIsMember(study, account))
                .isManager(getIsManager(study, account))
                .build();
    }

    private static boolean getisJoinable(Study study, Account account) {
        return study.isPublished() && study.isRecruiting()
                && study.getMembers().stream().filter(d -> d.getAccount().equals(account)).count() == 0
                && study.getMembers().stream().filter(d -> d.getAccount().equals(account)).count() == 0;
    }

    private static boolean getIsMember(Study study, Account account) {
        return study.getMembers().stream().filter(d -> d.getAccount().equals(account)).count() == 1;
    }

    private static boolean getIsManager(Study study, Account account) {
        return study.getManagers().stream().filter(d -> d.getAccount().equals(account)).count() == 1;
    }
}
