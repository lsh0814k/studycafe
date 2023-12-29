package com.studycafe.modules.study.event;

import com.studycafe.modules.account.domain.Account;
import com.studycafe.modules.account.repository.AccountPredicates;
import com.studycafe.modules.account.repository.AccountRepository;
import com.studycafe.modules.notification.domain.Notification;
import com.studycafe.modules.notification.repository.NotificationRepository;
import com.studycafe.modules.study.domain.Study;
import com.studycafe.modules.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.time.LocalDateTime;

import static com.studycafe.modules.notification.domain.NotificationType.STUDY_CREATED;
import static java.nio.charset.StandardCharsets.UTF_8;


@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class StudyEventLListener {
    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleStudyCreatedEvent(StudyCreatedEvent studyCreatedEvent) {
        Study study = studyRepository.findStudyWithTagsAndZonesById(studyCreatedEvent.getStudy().getId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디 입니다."));
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByTagsAndZones(study.getTags(), study.getZones()));
        accounts.forEach(account -> {
            if (account.isStudyCreatedByEmail()) {
                // TODO 이메일 발송
            }

            if (account.isStudyCreatedByWeb()) {
                saveStudyCreatedNotification(study, account);
            }
        });


        log.info("{} is created", study.getTitle());
    }

    private void saveStudyCreatedNotification(Study study, Account account) {
        Notification notification = Notification
                .builder()
                .title(study.getTitle())
                .link(String.format("/study/%s", URLEncoder.encode(study.getPath(), UTF_8)))
                .checked(false)
                .createdLocalDateTime(LocalDateTime.now())
                .message(study.getShortDescription())
                .account(account)
                .notificationType(STUDY_CREATED)
                .build();

        notificationRepository.save(notification);
    }
}
