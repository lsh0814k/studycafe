package com.studycafe.modules.study;

import com.studycafe.modules.account.domain.Account;
import com.studycafe.modules.study.domain.Study;
import com.studycafe.modules.study.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyFactory {
    private final StudyService studyService;

    public Study createStudy(Account account) {
        Study study = Study.builder()
                .path("test-path")
                .title("study title")
                .fullDescription("short description of a study")
                .shortDescription("full description of a study")
                .build();
        studyService.createNewStudy(account, study);

        return study;
    }
}
