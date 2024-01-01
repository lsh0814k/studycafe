package com.studycafe.modules.event.event;

import com.studycafe.modules.account.domain.Account;
import com.studycafe.modules.event.domain.Event;

public class EnrollmentRejectEvent extends EnrollmentEvent {
    public EnrollmentRejectEvent(Event event, Account account) {
        super(event, account, "모임 참가 신청을 거절했습니다.");
    }
}
