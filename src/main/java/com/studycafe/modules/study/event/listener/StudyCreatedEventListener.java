package com.studycafe.modules.study.event.listener;

import com.studycafe.infra.config.AppProperties;
import com.studycafe.infra.mail.EmailMessage;
import com.studycafe.infra.mail.EmailService;
import com.studycafe.modules.account.domain.Account;
import com.studycafe.modules.account.repository.AccountPredicates;
import com.studycafe.modules.account.repository.AccountRepository;
import com.studycafe.modules.notification.domain.Notification;
import com.studycafe.modules.notification.repository.NotificationRepository;
import com.studycafe.modules.study.domain.Study;
import com.studycafe.modules.study.event.StudyCreatedEvent;
import com.studycafe.modules.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.net.URLEncoder;
import java.time.LocalDateTime;

import static com.studycafe.modules.notification.domain.NotificationType.STUDY_CREATED;
import static java.nio.charset.StandardCharsets.UTF_8;


@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class StudyCreatedEventListener {
    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    @EventListener
    public void handleStudyCreatedEvent(StudyCreatedEvent studyCreatedEvent) {
        Study study = studyRepository.findStudyWithTagsAndZonesById(studyCreatedEvent.getStudy().getId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디 입니다."));
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByTagsAndZones(study.getTags(), study.getZones()));
        accounts.forEach(account -> {
            if (account.isStudyCreatedByEmail()) {
                sendEmail(account, study);
            }

            if (account.isStudyCreatedByWeb()) {
                saveStudyCreatedNotification(study, account);
            }
        });


        log.info("{} is created", study.getTitle());
    }

    private void sendEmail(Account account, Study study) {
        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getEmail())
                .subject(String.format("스터디 카페, %s 스터디가 개설되었습니다.", study.getTitle()))
                .message(createMailMessage(account, study))
                .build();

        emailService.sendEmail(emailMessage);
    }

    private String createMailMessage(Account account, Study study) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", String.format("/study/%s", URLEncoder.encode(study.getPath(), UTF_8)));
        context.setVariable("linkName", study.getTitle());
        context.setVariable("message", "새로운 스터디가 생겼습니다.");
        context.setVariable("host", appProperties.getHost());

        return templateEngine.process("mail/simple-link", context);
    }


    private void saveStudyCreatedNotification(Study study, Account account) {
        Notification notification = Notification
                .builder()
                .title(study.getTitle())
                .link(String.format("/study/%s", URLEncoder.encode(study.getPath(), UTF_8)))
                .checked(false)
                .createdDateTime(LocalDateTime.now())
                .message(study.getShortDescription())
                .account(account)
                .notificationType(STUDY_CREATED)
                .build();

        notificationRepository.save(notification);
    }
}
