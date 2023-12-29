package com.studycafe.modules.notification.repository;

import com.studycafe.modules.account.domain.Account;
import com.studycafe.modules.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Long countByAccountAndChecked(Account account, boolean checked);
}
