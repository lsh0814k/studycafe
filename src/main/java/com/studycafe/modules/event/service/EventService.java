package com.studycafe.modules.event.service;

import com.studycafe.modules.account.domain.Account;
import com.studycafe.modules.event.domain.Event;
import com.studycafe.modules.event.repository.EventRepository;
import com.studycafe.modules.study.event.StudyUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Event createEvent(Event event) {
        eventRepository.save(event);
        eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(), String.format("[%s] 모임을 만들었습니다.", event.getTitle())));

        return event;
    }

    public void updateEvent(Long id, Event event) {
        Event findEvent = checkExistEvent(eventRepository.findWithStudyById(id));
        findEvent.updateEvent(event);
        findEvent.acceptNextWaitingEnrollments();

        eventPublisher.publishEvent(new StudyUpdateEvent(findEvent.getStudy(), String.format("[%s] 모임 정보를 수정했습니다.", event.getTitle())));
    }

    public void cancelEvent(Long id) {
        Event event = checkExistEvent(eventRepository.findWithStudyById(id));
        eventRepository.delete(event);
        eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(), String.format("[%s] 모임을 취소했습니다.", event.getTitle())));
    }

    public void newEnrollment(Account account, Long id) {
        Event event = checkExistEvent(eventRepository.findWithEnrollmentById(id));
        event.addEnrollment(account);
    }

    public void cancelEnrollment(Account account, Long id) {
        Event event = checkExistEvent(eventRepository.findWithEnrollmentById(id));
        event.removeEnrollment(account);
    }

    public void acceptEnrollment(Long eventId, Long enrollmentId, Account account) {
        Event event = checkExistEvent(eventRepository.findWithEnrollmentById(eventId));
        event.acceptEnrollment(enrollmentId, account);
    }

    public void rejectEnrollment(Long eventId, Long enrollmentId, Account account) {
        Event event = checkExistEvent(eventRepository.findWithEnrollmentById(eventId));
        event.rejectEnrollment(enrollmentId, account);
    }

    public void checkInEnrollment(Long eventId, Long enrollmentId, Account account) {
        Event event = checkExistEvent(eventRepository.findWithEnrollmentById(eventId));
        event.checkInEnrollment(enrollmentId, account);
    }

    public void cancelCheckInEnrollment(Long eventId, Long enrollmentId, Account account) {
        Event event = checkExistEvent(eventRepository.findWithEnrollmentById(eventId));
        event.cancelCheckInEnrollment(enrollmentId, account);
    }

    private Event checkExistEvent(Optional<Event> eventRepository) {
        return eventRepository.orElseThrow(() -> new IllegalStateException("존재하지 않는 모임 입니다."));
    }


}
