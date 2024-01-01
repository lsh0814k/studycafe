package com.studycafe.modules.notification.domain;

import com.studycafe.modules.account.domain.Account;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.FetchType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter @NoArgsConstructor(access = PRIVATE) @EqualsAndHashCode(of="id")
@AllArgsConstructor @Builder
public class Notification {

    @Id @GeneratedValue
    @Column(name = "notification_id")
    private Long id;

    private String title;

    private String link;

    private String message;

    private Boolean checked;

    @ManyToOne(fetch = LAZY)
    private Account account;

    private LocalDateTime createdDateTime;

    @Enumerated(value = STRING)
    private NotificationType notificationType;

    public void readNotification() {
        this.checked = true;
    }
}
