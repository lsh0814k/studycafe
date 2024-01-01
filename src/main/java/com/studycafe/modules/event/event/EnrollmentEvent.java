package com.studycafe.modules.event.event;

import com.studycafe.modules.account.domain.Account;
import com.studycafe.modules.event.domain.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public abstract class EnrollmentEvent {
    protected final Event event;
    protected final Account account;
    protected final String message;
}
