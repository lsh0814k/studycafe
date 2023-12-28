package com.studycafe.modules.event.domain;

import com.studycafe.modules.account.domain.Account;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.*;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter @AllArgsConstructor
@Builder @EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = PROTECTED)
public class Enrollment {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(nullable = false)
    private LocalDateTime enrolledAt;

    @Column(nullable = false)
    private boolean accepted;

    @Column(nullable = false)
    private boolean attended;

    public void changeAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public void changeAttended(boolean attended) {
        this.attended = attended;
    }
}
