package com.studcafe.event.web.dto;

import com.studcafe.study.domain.Study;
import lombok.*;

import static lombok.AccessLevel.*;

@Getter @Setter @NoArgsConstructor
@AllArgsConstructor(access = PRIVATE) @Builder
public class EventStudyInfo {
    private String title;
    private String path;
    private Boolean useBanner;

    public static EventStudyInfo create(Study study) {
        return EventStudyInfo.builder()
                .path(study.getPath())
                .title(study.getTitle())
                .useBanner(study.isUseBanner())
                .build();
    }
}
