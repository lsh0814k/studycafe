package com.studycafe.modules.event.web.dto;

import com.studycafe.modules.account.domain.Account;
import com.studycafe.modules.study.domain.Study;
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
