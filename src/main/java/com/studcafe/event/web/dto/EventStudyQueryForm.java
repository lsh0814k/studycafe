package com.studcafe.event.web.dto;

import com.studcafe.account.domain.Account;
import com.studcafe.study.domain.Study;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static lombok.AccessLevel.*;

@Getter @AllArgsConstructor(access = PRIVATE)
@Builder
public class EventStudyQueryForm {
    private String path;
    private String title;
    private Boolean isManager;
    private Boolean useBanner;


    public static EventStudyQueryForm create(Study study, Account account) {
        return EventStudyQueryForm.builder()
                .path(study.getPath())
                .title(study.getTitle())
                .isManager(study.isManagerOf(account))
                .useBanner(study.isUseBanner())
                .build();
    }
}
