package com.studycafe.modules.event.web.validator;

import com.studycafe.modules.event.domain.Event;
import com.studycafe.modules.event.repository.EventRepository;
import com.studycafe.modules.event.web.dto.EventEditForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EventEditFormValidator implements Validator {
    private final EventRepository eventRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return EventEditForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EventEditForm eventEditForm = (EventEditForm) target;
        Event event = eventRepository.findById(eventEditForm.getId()).orElseThrow();
        if (eventEditForm.getLimitOfEnrollments() < event.getNumberOfAcceptedEnrollments()) {
            errors.rejectValue("limitOfEnrollments", "wrong.value", "확인된 참가 신청보다 모집 인원 수가 커야 합니다.");
        }

        if (isNotValidEndEnrollmentDateTime(eventEditForm)) {
            errors.rejectValue("endEnrollmentDateTime", "wrong.datetime", "모임 접수 종료 일시를 정확히 입력하세요.");
        }

        if (isNotValidEndDateTime(eventEditForm)) {
            errors.rejectValue("endEnrollmentDateTime", "wrong.datetime", "모임 종료 일시를 정확히 입력하세요.");
        }

        if (isNotValidStartDateTime(eventEditForm)) {
            errors.rejectValue("endEnrollmentDateTime", "wrong.datetime", "모임 시작 일시를 정확히 입력하세요.");
        }
    }

    private boolean isNotValidStartDateTime(EventEditForm eventEditForm) {
        return eventEditForm.getStartDateTime().isBefore(eventEditForm.getEndEnrollmentDateTime());
    }

    private boolean isNotValidEndEnrollmentDateTime(EventEditForm eventEditForm) {
        return eventEditForm.getEndEnrollmentDateTime().isBefore(LocalDateTime.now());
    }

    private boolean isNotValidEndDateTime(EventEditForm eventEditForm) {
        LocalDateTime endDateTime = eventEditForm.getEndDateTime();
        return endDateTime.isBefore(eventEditForm.getStartDateTime()) || endDateTime.isBefore(eventEditForm.getEndEnrollmentDateTime());
    }
}
