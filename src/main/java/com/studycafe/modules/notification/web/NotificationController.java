package com.studycafe.modules.notification.web;

import com.studycafe.modules.account.annotation.CurrentUser;
import com.studycafe.modules.account.domain.Account;
import com.studycafe.modules.notification.domain.Notification;
import com.studycafe.modules.notification.domain.NotificationType;
import com.studycafe.modules.notification.repository.NotificationRepository;
import com.studycafe.modules.notification.service.NotificationService;
import com.studycafe.modules.notification.web.dto.NotificationForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;


    @GetMapping("/notifications")
    public String notifications(@CurrentUser Account account, Model model) {
        long numberOfChecked = notificationRepository.countByAccountAndChecked(account, true);
        List<Notification> notifications = notificationService.getNoCheckedNotifications(account);

        putCategorizedNotifications(model, notifications, numberOfChecked, notifications.size());
        model.addAttribute("isNew", true);

        return "notification/list";
    }

    @GetMapping("/notifications/old")
    public String oldNotifications(@CurrentUser Account account, Model model) {
        List<Notification> notifications = notificationRepository.findByAccountAndCheckedOrderByCreatedDateTimeDesc(account, true);
        long numberOfNotChecked = notificationRepository.countByAccountAndChecked(account, false);

        putCategorizedNotifications(model, notifications, notifications.size(), numberOfNotChecked);
        model.addAttribute("isNew", false);

        return "notification/list";
    }

    private void putCategorizedNotifications(Model model, List<Notification> notifications, long numberOfChecked, long numberOfNotChecked) {
        List<NotificationForm> newStudyNotifications = new ArrayList<>();
        List<NotificationForm> eventEnrollmentNotifications = new ArrayList<>();
        List<NotificationForm> watchingStudyNotifications = new ArrayList<>();

        for (Notification notification: notifications) {
            switch (notification.getNotificationType()) {
                case STUDY_CREATED: newStudyNotifications.add(new NotificationForm(notification)); break;
                case EVENT_ENROLLMENT: eventEnrollmentNotifications.add(new NotificationForm(notification)); break;
                case STUDY_UPDATED: watchingStudyNotifications.add(new NotificationForm(notification)); break;
            }
        }

        model.addAttribute("numberOfNotChecked", numberOfNotChecked);
        model.addAttribute("numberOfChecked", numberOfChecked);
        model.addAttribute("notifications", notifications);
        model.addAttribute("newStudyNotifications", newStudyNotifications);
        model.addAttribute("eventEnrollmentNotifications", eventEnrollmentNotifications);
        model.addAttribute("watchingStudyNotifications", watchingStudyNotifications);
    }

    @PostMapping("/notifications/delete")
    public String deleteNotifications(@CurrentUser Account account) {

        notificationService.deleteNotifications(account);
        return "redirect:/notifications/old";
    }

    private List<NotificationForm> findFilterNotification(List<Notification> notifications, NotificationType notificationType) {
        return notifications.stream()
                .filter(n -> n.getNotificationType() == notificationType)
                .map(NotificationForm::new)
                .toList();
    }
}
