package com.studycafe.modules.event.web.dto;

import com.studycafe.modules.event.domain.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Getter @AllArgsConstructor(access = PRIVATE)
@Builder
public class EventEventsForm {
    private Long id;
    private String title;
    private LocalDateTime endDateTime;
    private LocalDateTime startDateTime;
    private Integer limitOfEnrollments;
    private Long numberOfRemainSpots;
    private LocalDateTime endEnrollmentDateTime;

    public static EventEventsForm create(Event event) {
        return EventEventsForm.builder()
                .id(event.getId())
                .title(event.getTitle())
                .endDateTime(event.getEndDateTime())
                .startDateTime(event.getStartDateTime())
                .endEnrollmentDateTime(event.getEndEnrollmentDateTime())
                .limitOfEnrollments(event.getLimitOfEnrollments())
                .numberOfRemainSpots(event.getNumberOfAcceptedEnrollments())
                .build();
    }
}
