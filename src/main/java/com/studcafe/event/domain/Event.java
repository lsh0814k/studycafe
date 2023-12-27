package com.studcafe.event.domain;

import com.studcafe.account.domain.Account;
import com.studcafe.study.domain.Study;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.FetchType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = PROTECTED)
public class Event {
    @Id @GeneratedValue
    @Column(name = "event_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "account_id")
    private Account createdBy;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    @Column(nullable = false)
    private LocalDateTime createDateTime;

    @Column(nullable = false)
    private LocalDateTime endEnrollmentDateTime;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    private Integer limitOfEnrollments;

    @Builder.Default
    @OneToMany(mappedBy = "event")
    private List<Enrollment> enrollments = new ArrayList();

    @Enumerated(value = STRING)
    private EventType eventType;

    public boolean isEnrollableFor(Account account) {
        return isNotClosed() && !isAlreadyEnrolled(account);
    }

    public boolean isDisenrollableFor(Account account) {
        return isNotClosed() && isAlreadyEnrolled(account);
    }

    public boolean isAttended(Account account) {
        return enrollments.stream()
                .filter(e -> e.equals(account) && e.isAttended())
                .count() > 0;
    }

    public boolean canAccept(Enrollment enrollment) {
        return this.eventType == EventType.CONFIRMATIVE
                && this.enrollments.contains(enrollment)
                && this.limitOfEnrollments > this.getNumberOfAcceptedEnrollments()
                && !enrollment.isAttended()
                && !enrollment.isAccepted();
    }

    public boolean canReject(Enrollment enrollment) {
        return this.eventType == EventType.CONFIRMATIVE
                && this.enrollments.contains(enrollment)
                && !enrollment.isAttended()
                && enrollment.isAccepted();
    }

    public long getNumberOfAcceptedEnrollments() {
        return this.enrollments.stream().filter(Enrollment::isAccepted).count();
    }

    private boolean isNotClosed() {
        return this.endEnrollmentDateTime.isAfter(LocalDateTime.now());
    }

    private boolean isAlreadyEnrolled(Account account) {
        return enrollments.stream()
                .filter(e -> e.equals(account))
                .count() > 1;
    }

    public void updateEvent(Event event) {
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.endEnrollmentDateTime = event.getEndEnrollmentDateTime();
        this.startDateTime = event.getStartDateTime();
        this.endDateTime = event.getEndDateTime();
        this.limitOfEnrollments = event.getLimitOfEnrollments();
    }
}
