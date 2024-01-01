package com.studycafe.modules.study.event;

import com.studycafe.modules.study.domain.Study;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;

@Getter
@RequiredArgsConstructor
public class StudyUpdateEvent {
    private final Study study;
    private final String message;

}
