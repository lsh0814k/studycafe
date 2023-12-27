package com.studcafe.event.service;

import com.studcafe.account.domain.Account;
import com.studcafe.event.domain.Event;
import com.studcafe.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        // TODO 모집 인원을 늘린 선착순 모임의 경우에, 자동으로 추가 인원의 참가 신청을 확정 상태로 변경해야 한다.
    }

    public void cancelEvent(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new IllegalStateException("존재하지 않는 모임 입니다."));
        eventRepository.delete(event);
    }

    public void newEnrollment(Account account, Long id) {
        Event event = eventRepository.findWithEnrollmentById(id).orElseThrow(() -> new IllegalStateException("존재하지 않는 모임 입니다."));
        event.addEnrollment(account);
    }

    public void cancelEnrollment(Account account, Long id) {
        Event event = eventRepository.findWithEnrollmentById(id).orElseThrow(() -> new IllegalStateException("존재하지 않는 모임 입니다."));
        event.removeEnrollment(account);
    }
}
