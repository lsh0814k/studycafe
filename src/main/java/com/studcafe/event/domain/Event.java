package com.studcafe.event.domain;

import com.studcafe.account.domain.Account;
import com.studcafe.study.domain.Study;
import jakarta.persistence.*;
import lombok.*;

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
    private Account createBy;

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
}
