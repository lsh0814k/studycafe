package com.studcafe.event.web.dto;

import com.studcafe.account.domain.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static lombok.AccessLevel.PRIVATE;

@Getter @Builder
@AllArgsConstructor(access = PRIVATE)
public class EventAccountForm {
    private String nickname;
    private String profileImage;

    public static EventAccountForm create(Account account) {
        return EventAccountForm.builder()
                .nickname(account.getNickname())
                .profileImage(account.getProfileImage())
                .build();
    }

}
