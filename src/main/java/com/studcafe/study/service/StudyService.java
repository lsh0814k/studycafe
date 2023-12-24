package com.studcafe.study.service;

import com.studcafe.account.domain.Account;
import com.studcafe.study.domain.Study;
import com.studcafe.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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

    public void updateStudyDescription(Long id, Study study) {
        Study findStudy = studyRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디 입니다."));
        findStudy.updateDescription(study);
    }

    public Study getStudyToUpdate(String path, Account account) {
        Study study = studyRepository.findByPath(path).orElseThrow(() -> new IllegalStateException("존재하지 않는 스터디 입니다."));
        if (!study.isManagerOf(account)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }

        return study;
    }
}
