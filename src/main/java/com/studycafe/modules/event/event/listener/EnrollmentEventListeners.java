package com.studycafe.modules.event.event.listener;

import com.studycafe.infra.config.AppProperties;
import com.studycafe.infra.mail.EmailMessage;
import com.studycafe.infra.mail.EmailService;
import com.studycafe.modules.account.domain.Account;
import com.studycafe.modules.event.domain.Event;
import com.studycafe.modules.event.event.EnrollmentEvent;
import com.studycafe.modules.event.repository.EventRepository;
import com.studycafe.modules.notification.domain.Notification;
import com.studycafe.modules.notification.repository.NotificationRepository;
import com.studycafe.modules.study.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.net.URLEncoder;
import java.time.LocalDateTime;

import static com.studycafe.modules.notification.domain.NotificationType.EVENT_ENROLLMENT;
import static java.nio.charset.StandardCharsets.UTF_8;

@Async
@Component
@Transactional
@RequiredArgsConstructor
public class EnrollmentEventListeners {
    private final EventRepository eventRepository;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final AppProperties appProperties;
    private final TemplateEngine templateEngine;

    @EventListener
    public void handleEnrollment(EnrollmentEvent enrollmentEvent) {
        Event event = eventRepository.findWithStudyById(enrollmentEvent.getEvent().getId()).orElseThrow(() -> new IllegalStateException("존재하지 않는 모임 입니다."));
        Account account = enrollmentEvent.getAccount();
        String message = enrollmentEvent.getMessage();
        if (account.isStudyEnrollmentResultByWeb()) {
            createNotification(event, account, message);
        }

        if (account.isStudyEnrollmentResultByEmail()) {
            sendEmail(event, account, message);
        }
    }

    private void sendEmail(Event event, Account account, String message) {
        Study study = event.getStudy();
        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getEmail())
                .subject(String.format("스터디 카페, %s 스터디에 새로운 소식이 있습니다.", study.getTitle()))
                .message(createMailMessage(event, account, study, message))
                .build();

        emailService.sendEmail(emailMessage);
    }

    private String createMailMessage(Event event, Account account, Study study, String message) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", String.format("/study/%s/events/%s", URLEncoder.encode(study.getPath(), UTF_8), event.getId()));
        context.setVariable("linkName", event.getTitle());
        context.setVariable("message", message);
        context.setVariable("host", appProperties.getHost());

        return templateEngine.process("mail/simple-link", context);
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
