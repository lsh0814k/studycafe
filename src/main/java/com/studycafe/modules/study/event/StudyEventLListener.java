package com.studycafe.modules.study.event;

import com.studycafe.modules.study.domain.Study;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Slf4j
@Async
@Component
public class StudyEventLListener {
    @EventListener
    public void handleStudyCreatedEvent(StudyCreatedEvent studyCreatedEvent) {
        Study study = studyCreatedEvent.getStudy();
        log.info("{} is created", study.getTitle());
    }
}
