package com.studycafe.modules.study.event;

import com.studycafe.modules.account.domain.Account;
import com.studycafe.modules.notification.domain.Notification;
import com.studycafe.modules.notification.repository.NotificationRepository;
import com.studycafe.modules.study.domain.Study;
import com.studycafe.modules.study.domain.StudyManager;
import com.studycafe.modules.study.domain.StudyMember;
import com.studycafe.modules.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.studycafe.modules.notification.domain.NotificationType.STUDY_UPDATED;
import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class StudyUpdateEventListener {
    private final StudyRepository studyRepository;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleStudyUpdatedEvent(StudyUpdateEvent studyUpdateEvent) {
        Study study = studyRepository.findStudyWithManagersAndMembersById(studyUpdateEvent.getStudy().getId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디 입니다."));
        List<Account> accounts = new ArrayList<>();
        accounts.addAll(study.getManagers().stream().map(StudyManager::getAccount).toList());
        accounts.addAll(study.getMembers().stream().map(StudyMember::getAccount).toList());
        accounts.forEach(account -> {
            if (account.isStudyUpdatedByEmail()) {
                // TODO 이메일 발송
            }

            if (account.isStudyUpdatedByWeb()) {
                createNotification(study, account, studyUpdateEvent.getMessage());
            }
        });

        log.info("{} is updated", study.getTitle());
    }

    private void createNotification(Study study, Account account, String message) {
        Notification notification = Notification
                .builder()
                .title(study.getTitle())
                .link(String.format("/study/%s", URLEncoder.encode(study.getPath(), UTF_8)))
                .checked(false)
                .createdDateTime(LocalDateTime.now())
                .message(message)
                .account(account)
                .notificationType(STUDY_UPDATED)
                .build();

        notificationRepository.save(notification);
    }
}
