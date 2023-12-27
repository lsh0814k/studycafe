package com.studcafe.event.web.dto;

import com.studcafe.event.domain.Enrollment;
import com.studcafe.event.domain.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Getter @Builder
@AllArgsConstructor(access = PRIVATE)
public class EnrollmentForm {
    private EventAccountForm account;
    private LocalDateTime enrolledAt;
    private Boolean accepted;
    private Boolean isCanAccept;
    private Boolean isCanReject;

    public static EnrollmentForm create(Event event, Enrollment enrollment) {
        return EnrollmentForm.builder()
                .account(EventAccountForm.create(enrollment.getAccount()))
                .enrolledAt(enrollment.getEnrolledAt())
                .accepted(enrollment.isAccepted())
                .isCanAccept(event.canAccept(enrollment))
                .isCanReject(event.canReject(enrollment))
                .build();
    }
}