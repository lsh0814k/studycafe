package com.studcafe.study.service;

import com.studcafe.account.domain.Account;
import com.studcafe.study.domain.Study;
import com.studcafe.study.repository.StudyRepository;
import com.studcafe.tag.domain.Tag;
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
        checkIfManager(account, study);

        return study;
    }

    @Transactional(readOnly = true)
    public Study getStudyToView(String path, Account account) {
        Study study = studyRepository.findAllByPath(path).orElseThrow(() -> new IllegalStateException("존재하지 않는 스터디 입니다."));
        checkIfManager(account, study);

        return study;
    }

    public void updateUseBanner(Long id, boolean useBanner) {
        Study findStudy = studyRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디 입니다."));
        findStudy.updateUseBanner(useBanner);
    }

    public void updateBanner(Long id, String image) {
        Study findStudy = studyRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디 입니다."));
        findStudy.updateBanner(image);
    }

    public Study getStudyToUpdateTag(Account account, String path) {
        Study study = studyRepository.findAccountWithTagsByPath(path).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디 입니다."));
        checkIfManager(account, study);

        return study;
    }

    public void addTag(Study study, Tag tag) {
        study.getTags().add(tag);
    }

    public void removeTag(Study study, Tag tag) {
        study.getTags().remove(tag);
    }

    private void checkIfManager(Account account, Study study) {
        if (!study.isManagerOf(account)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
    }
}
