package com.studcafe.study.service;

import com.studcafe.account.domain.Account;
import com.studcafe.account.repository.AccountRepository;
import com.studcafe.account.service.AccountService;
import com.studcafe.study.domain.Study;
import com.studcafe.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;

    public void createNewStudy(Account account, Study study) {
        study.addManger(account);
        studyRepository.save(study);
    }
}
