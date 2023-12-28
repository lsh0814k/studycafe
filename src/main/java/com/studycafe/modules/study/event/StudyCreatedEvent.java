package com.studycafe.modules.study.event;

import com.studycafe.modules.study.domain.Study;
import lombok.Getter;

@Getter
public class StudyCreatedEvent {

    private Study study;

    public StudyCreatedEvent(Study study) {
        this.study = study;
    }
}
