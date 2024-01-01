package com.studycafe.modules.event.event;

import com.studycafe.modules.account.domain.Account;
import com.studycafe.modules.event.domain.Event;

public class EnrollmentAcceptEvent extends EnrollmentEvent {
    public EnrollmentAcceptEvent(Event event, Account account) {
        super(event, account, "모임 참가 신청을 확인했습니다. 모임에 참석하세요.");
    }
}
