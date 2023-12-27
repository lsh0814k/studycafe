package com.studcafe.event.web.dto;

import com.studcafe.event.domain.Event;
import com.studcafe.event.domain.EventType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter @NoArgsConstructor
@AllArgsConstructor @Builder
public class EventEditForm {
    @NotNull
    private Long id;
    @NotBlank
    @Length(max = 50)
    private String title;
    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endEnrollmentDateTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDateTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDateTime;

    @Min(2)
    private Integer limitOfEnrollments;

    public static EventEditForm create(Event event) {
        return EventEditForm.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .endEnrollmentDateTime(event.getEndEnrollmentDateTime())
                .startDateTime(event.getStartDateTime())
                .endDateTime(event.getEndDateTime())
                .limitOfEnrollments(event.getLimitOfEnrollments())
                .build();
    }

    public Event createEvent() {
        return Event.builder()
                .title(this.title)
                .description(this.description)
                .startDateTime(this.startDateTime)
                .endDateTime(this.endDateTime)
                .endEnrollmentDateTime(this.endEnrollmentDateTime)
                .limitOfEnrollments(this.limitOfEnrollments)
                .build();
    }
}
