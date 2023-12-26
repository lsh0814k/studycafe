package com.studcafe.event.service;

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
}
