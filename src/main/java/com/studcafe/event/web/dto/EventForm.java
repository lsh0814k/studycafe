package com.studcafe.event.web.dto;

import com.studcafe.account.domain.Account;
import com.studcafe.event.domain.Event;
import com.studcafe.event.domain.EventType;
import com.studcafe.study.domain.Study;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
public class EventForm {
    @NotBlank
    @Length(max = 50)
    private String title;
    private String description;
    private EventType eventType = EventType.FCFS;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endEnrollmentDateTime = LocalDateTime.now();

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDateTime = LocalDateTime.now();

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDateTime = LocalDateTime.now();

    @Min(2)
    private Integer limitOfEnrollments = 2;

    public Event createEvent(Study study, Account account) {
        return Event.builder()
                .title(title)
                .description(description)
                .eventType(eventType)
                .endEnrollmentDateTime(endEnrollmentDateTime)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .limitOfEnrollments(limitOfEnrollments)
                .study(study)
                .createdBy(account)
                .createDateTime(LocalDateTime.now())
                .build();
    }
}
