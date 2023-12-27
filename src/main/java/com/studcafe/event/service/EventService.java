package com.studcafe.event.service;

import com.studcafe.account.domain.Account;
import com.studcafe.event.domain.Event;
import com.studcafe.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Event createEvent(Event event) {
        eventRepository.save(event);
        return event;
    }

    public void updateEvent(Long id, Event event) {
        Event findEvent = eventRepository.findById(id).orElseThrow();
        findEvent.updateEvent(event);

        event.acceptNextWaitingEnrollments();
    }

    public void cancelEvent(Long id) {
        Event event = checkExistEvent(eventRepository.findById(id));
        eventRepository.delete(event);
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
