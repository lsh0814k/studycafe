package com.studcafe.event.domain;

import com.studcafe.account.domain.Account;
import com.studcafe.study.domain.Study;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static jakarta.persistence.CascadeType.*;
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
    @OneToMany(mappedBy = "event", orphanRemoval = true, cascade = ALL)
    @OrderBy(value = "enrolledAt desc")
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

    public void acceptNextWaitingEnrollments() {
        if (!isAbleToAcceptWaitingEnrollment()) {
            return;
        }

        long acceptEnrollmentCount = getNumberOfAcceptedEnrollments();
        List<Enrollment> enrollmentList = waitingList();
        int numberToAccept = (int) Math.min(limitOfEnrollments - acceptEnrollmentCount, waitingList().size());
        enrollmentList.subList(0, numberToAccept).forEach(e -> e.changeAccepted(true));
    }

    private List<Enrollment> waitingList() {
        return enrollments.stream()
                .filter(e -> !e.isAccepted())
                .toList();
    }

    public void addEnrollment(Account account) {
        existEnrollmentThrowsException(account);

        Enrollment enrollment = Enrollment.builder()
                .event(this)
                .enrolledAt(LocalDateTime.now())
                .account(account)
                .attended(false)
                .accepted(isAbleToAcceptWaitingEnrollment())
                .build();

        this.enrollments.add(enrollment);
    }

    public void removeEnrollment(Account account) {
        enrollments.remove(findEnrollment(account));

        acceptNextWaitingEnrollment();
    }

    private void acceptNextWaitingEnrollment() {
        if (!isAbleToAcceptWaitingEnrollment()) {
            return;
        }

        Optional<Enrollment> enrollmentOptional = getTheFirstWaitingEnrollment();
        if (enrollmentOptional.isEmpty()){
            return;
        }

        enrollmentOptional.get().changeAccepted(true);
    }

    private Optional<Enrollment> getTheFirstWaitingEnrollment() {
        if (enrollments.isEmpty()) {
            Optional.empty();
        }

        return enrollments.stream().filter(e -> !e.isAccepted()).findFirst();
    }

    private Enrollment findEnrollment(Account account) {
        return enrollments.stream()
                .filter(e -> e.getAccount().equals(account))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("모임에 참가자 명단에 없습니다."));
    }

    private void existEnrollmentThrowsException(Account account) {
        Optional<Enrollment> any = enrollments.stream()
                .filter(e -> e.getAccount().equals(account))
                .findAny();

        if (any.isPresent()) {
            throw new IllegalStateException("이미 모입에 참여하고 있습니다.");
        }
    }

    private boolean isAbleToAcceptWaitingEnrollment() {
        return this.eventType == EventType.FCFS && this.limitOfEnrollments > this.getNumberOfAcceptedEnrollments();
    }
}
