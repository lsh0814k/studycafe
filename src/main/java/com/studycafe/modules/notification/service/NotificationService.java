package com.studycafe.modules.notification.service;

import com.studycafe.modules.account.domain.Account;
import com.studycafe.modules.notification.domain.Notification;
import com.studycafe.modules.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public void deleteNotifications(Account account) {
        notificationRepository.deleteAllByAccountAndChecked(account, true);
    }

    private void markAsRead(List<Notification> notifications) {
        for (Notification notification: notifications) {
            notification.readNotification();
        }
    }

    public List<Notification> getNoCheckedNotifications(Account account) {
        List<Notification> notifications = notificationRepository.findByAccountAndCheckedOrderByCreatedDateTimeDesc(account, false);
        markAsRead(notifications);

        return notifications;
    }
}
