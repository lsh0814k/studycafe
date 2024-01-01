package com.studycafe.modules.event.event.listener;

import com.studycafe.modules.account.domain.Account;
import com.studycafe.modules.account.repository.AccountPredicates;
import com.studycafe.modules.account.repository.AccountRepository;
import com.studycafe.modules.event.domain.Event;
import com.studycafe.modules.event.event.EnrollmentEvent;
import com.studycafe.modules.event.repository.EventRepository;
import com.studycafe.modules.notification.domain.Notification;
import com.studycafe.modules.notification.domain.NotificationType;
import com.studycafe.modules.notification.repository.NotificationRepository;
import com.studycafe.modules.study.domain.Study;
import com.studycafe.modules.study.event.StudyCreatedEvent;
import com.studycafe.modules.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.time.LocalDateTime;

import static com.studycafe.modules.notification.domain.NotificationType.EVENT_ENROLLMENT;
import static com.studycafe.modules.notification.domain.NotificationType.STUDY_CREATED;
import static java.nio.charset.StandardCharsets.UTF_8;

@Async
@Component
@Transactional
@RequiredArgsConstructor
public class EnrollmentEventListeners {
    private final EventRepository eventRepository;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleEnrollment(EnrollmentEvent enrollmentEvent) {
        Event event = eventRepository.findWithStudyById(enrollmentEvent.getEvent().getId()).orElseThrow(() -> new IllegalStateException("존재하지 않는 모임 입니다."));
        Account account = enrollmentEvent.getAccount();
        String message = enrollmentEvent.getMessage();
        if (account.isStudyEnrollmentResultByWeb()) {
            createNotification(event, account, message);
        }

        if (account.isStudyEnrollmentResultByEmail()) {
        }
    }

    private void createNotification(Event event, Account account, String message) {
        Study study = event.getStudy();
        Notification notification = Notification
                .builder()
                .title(event.getTitle())
                .link(String.format("/study/%s/events/%s", URLEncoder.encode(study.getPath(), UTF_8), event.getId()))
                .checked(false)
                .createdDateTime(LocalDateTime.now())
                .message(message)
                .account(account)
                .notificationType(EVENT_ENROLLMENT)
                .build();

        notificationRepository.save(notification);
    }
}
