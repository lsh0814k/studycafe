package com.studcafe.account.web.dto;

import com.studcafe.account.domain.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class Notifications {
    private boolean studyCreatedByEmail;
    private boolean studyCreatedByWeb;
    private boolean studyEnrollmentResultByEmail;
    private boolean studyEnrollmentResultByWeb;
    private boolean studyUpdatedByEmail;
    private boolean studyUpdatedByWeb;

    public Notifications(Account account) {
        this.studyCreatedByEmail = account.isStudyCreatedByEmail();
        this.studyCreatedByWeb = account.isStudyCreatedByWeb();
        this.studyEnrollmentResultByEmail = account.isStudyEnrollmentResultByEmail();
        this.studyEnrollmentResultByWeb = account.isStudyEnrollmentResultByWeb();
        this.studyUpdatedByEmail = account.isStudyUpdatedByEmail();
        this.studyUpdatedByWeb = account.isStudyUpdatedByWeb();
    }

    public Account createAccount() {
        return Account.builder()
                .studyCreatedByEmail(studyCreatedByEmail)
                .studyCreatedByWeb(studyCreatedByWeb)
                .studyEnrollmentResultByEmail(studyEnrollmentResultByEmail)
                .studyEnrollmentResultByWeb(studyEnrollmentResultByWeb)
                .studyUpdatedByEmail(studyUpdatedByEmail)
                .studyUpdatedByWeb(studyUpdatedByWeb)
                .build();
    }
}
