package com.studcafe.study.web.validator;

import com.studcafe.study.repository.StudyRepository;
import com.studcafe.study.web.dto.StudyPathForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class StudyPathFormValidator implements Validator {
    private StudyRepository studyRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return StudyPathForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        StudyPathForm studyPathForm = (StudyPathForm) target;

        if (studyRepository.existsByPath(studyPathForm.getNewPath())) {
            errors.rejectValue("path", "wrong.path", "해당 스터디 경로를 사용할 수 없습니다.");
        }
    }
}
