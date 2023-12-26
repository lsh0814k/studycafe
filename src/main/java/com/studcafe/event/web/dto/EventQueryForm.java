package com.studcafe.event.web.dto;

import com.studcafe.account.domain.Account;
import com.studcafe.event.domain.Event;
import com.studcafe.event.domain.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor(access = PRIVATE) @Builder
public class EventQueryForm {
    private Long id;
    private String title;
    private String description;
    private EventType eventType;
    private Boolean isEnrollableFor;
    private Boolean isDisenrollableFor;
    private Boolean isAttended;
    private Boolean canAccept;
    private List<EnrollmentForm> enrollments;
    private Integer limitOfEnrollments;
    private LocalDateTime endEnrollmentDateTime;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Account createdBy;

    public static EventQueryForm create(Event event, Account account) {
        return EventQueryForm.builder()
                .id(event.getId())
                .title(event.getTitle())
                .eventType(event.getEventType())
                .isEnrollableFor(event.isEnrollableFor(account))
                .isDisenrollableFor(event.isDisenrollableFor(account))
                .isAttended(event.isAttended(account))
                .enrollments(event.getEnrollments().stream()
                        .map(e -> EnrollmentForm.create(event, e)).toList()
                )
                .limitOfEnrollments(event.getLimitOfEnrollments())
                .endEnrollmentDateTime(event.getEndEnrollmentDateTime())
                .startDateTime(event.getStartDateTime())
                .endDateTime(event.getEndDateTime())
                .createdBy(event.getCreatedBy())
                .build();
    }
}
