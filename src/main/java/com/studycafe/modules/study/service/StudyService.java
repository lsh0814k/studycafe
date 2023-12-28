package com.studycafe.modules.study.service;

import com.studycafe.modules.account.domain.Account;
import com.studycafe.modules.study.domain.Study;
import com.studycafe.modules.study.repository.StudyRepository;
import com.studycafe.modules.tag.domain.Tag;
import com.studycafe.modules.tag.service.TagService;
import com.studycafe.modules.zone.domain.Zone;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final TagService tagService;

    public void createNewStudy(Account account, Study study) {
        study.addManger(account);
        studyRepository.save(study);
    }

    public void updateStudyDescription(String path, Study sourceStudy, Account account) {
        Study study = getStudyToUpdate(path, account);
        study.updateDescription(sourceStudy);
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

    public void updateUseBanner(String path, Account account, Boolean useBanner) {
        Study study = getStudyToUpdate(path, account);
        study.updateUseBanner(useBanner);
    }

    public void updateBanner(String path, Account account, String image) {
        Study study = getStudyToUpdate(path, account);
        study.updateBanner(image);
    }

    public Study getStudyToUpdateTag(Account account, String path) {
        Study study = studyRepository.findStudyWithTagsByPath(path).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디 입니다."));
        checkIfManager(account, study);

        return study;
    }

    public void addTag(String path, Account account, String title) {
        Tag tag = tagService.findOrCreateNew(title);
        Study study = getStudyToUpdateTag(account, path);
        study.getTags().add(tag);
    }

    public void removeTag(String path, Account account, Tag tag) {
        Study study = getStudyToUpdateTag(account, path);
        study.getTags().remove(tag);
    }

    public Study getStudyToUpdateZone(Account account, String path) {
        Study study = studyRepository.findStudyWithZonesByPath(path).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디 입니다."));
        checkIfManager(account, study);

        return study;
    }

    private void checkIfManager(Account account, Study study) {
        if (!study.isManagerOf(account)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
    }

    public void addZone(String path, Account account, Zone zone) {
        Study study = getStudyToUpdateZone(account, path);
        study.getZones().add(zone);
    }

    public void removeZone(String path, Account account, Zone zone) {
        Study study = getStudyToUpdateZone(account, path);
        study.getZones().remove(zone);
    }

    public void publish(String path, Account account) {
        Study study = getStudyToUpdate(path, account);
        study.publish();
    }

    public void close(String path, Account account) {
        Study study = getStudyToUpdate(path, account);
        study.close();
    }

    public void updateStudyTitle(String path, Account account, String title) {
        Study study = getStudyToUpdate(path, account);
        study.updateStudyTitle(title);
    }

    public void updateStudyPath(String path, Account account, String newPath) {
        Study study = getStudyToUpdate(path, account);
        study.updateStudyPath(newPath);
    }

    public void remove(String path, Account account) {
        Study study = getStudyToUpdate(path, account);
        if (study.isRemovable()) {
            studyRepository.delete(study);
        } else {
            throw new IllegalArgumentException("스터디를 삭제할 수 없습니다.");
        }
    }

    public void addMember(String path, Account account) {
        Study study = getStudyToUpdateMember(path);
        study.addMember(account);
    }

    public void removeMember(String path, Account account) {
        Study study = getStudyToUpdateMember(path);
        study.removeMember(account);
    }

    public Study getStudyToUpdateMember(String path) {
        return studyRepository.getStudyToUpdateMember(path).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디 입니다."));
    }

    public void startRecruit(String path, Account account) {
        Study study = getStudyToUpdate(path, account);
        study.startRecruit();
    }

    public void stopRecruit(String path, Account account) {
        Study study = getStudyToUpdate(path, account);
        study.stopRecruit();
    }
}
