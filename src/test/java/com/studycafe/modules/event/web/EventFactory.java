package com.studycafe.modules.event.web;

import com.studycafe.modules.account.domain.Account;
import com.studycafe.modules.event.domain.Event;
import com.studycafe.modules.event.domain.EventType;
import com.studycafe.modules.event.service.EventService;
import com.studycafe.modules.study.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EventFactory {
    private final EventService eventService;

    public Event createEvent(Account account, Study study, EventType eventType, int limit) {
        Event event = Event.builder()
                .title("모임")
                .description("description")
                .eventType(eventType)
                .endEnrollmentDateTime(LocalDateTime.now().plusHours(1))
                .startDateTime(LocalDateTime.now().plusHours(2))
                .endDateTime(LocalDateTime.now().plusDays(2))
                .createDateTime(LocalDateTime.now())
                .study(study)
                .createdBy(account)
                .limitOfEnrollments(limit)
                .build();

        eventService.createEvent(event);
        return event;
    }
}
