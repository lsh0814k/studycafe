package com.studycafe.modules.notification.web.dto;

import com.studycafe.modules.notification.domain.Notification;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
public class NotificationForm {
    private String title;
    private LocalDateTime createdDateTime;
    private String message;
    private String link;

    public NotificationForm(Notification notification) {
        this.title = notification.getTitle();
        this.message = notification.getMessage();
        this.link = notification.getLink();
        createdDateTime = notification.getCreatedDateTime();
    }
}
