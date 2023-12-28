package com.studycafe.modules.event.web;

import com.studycafe.modules.account.domain.Account;
import com.studycafe.modules.event.domain.Event;
import com.studycafe.modules.event.repository.EventRepository;
import com.studycafe.modules.event.service.EventService;
import com.studycafe.modules.event.web.dto.*;
import com.studycafe.modules.event.web.validator.EventEditFormValidator;
import com.studycafe.modules.event.web.validator.EventFormValidator;
import com.studycafe.modules.account.annotation.CurrentUser;
import com.studycafe.modules.study.domain.Study;
import com.studycafe.modules.study.repository.StudyRepository;
import com.studycafe.modules.study.service.StudyService;
import com.studycafe.modules.study.web.dto.StudyQueryForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study/{path}")
public class EventController {

    private final StudyService studyService;
    private final EventService eventService;
    private final StudyRepository studyRepository;
    private final EventRepository eventRepository;
    private final EventEditFormValidator eventEditFormValidator;

    @InitBinder("eventForm")
    public void initBinderEventForm(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new EventFormValidator());
    }

    @InitBinder("eventEditForm")
    public void initBinderEventEditForm(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventEditFormValidator);
    }

    @GetMapping("/new-event")
    public String newEventForm(@CurrentUser Account account, @PathVariable("path") String path, Model model) {
        Study study = studyService.getStudyToUpdate(path, account);
        model.addAttribute(account);
        model.addAttribute("study", EventStudyInfo.create(study));
        model.addAttribute(new EventForm());

        return "event/form";
    }

    @PostMapping("/new-event")
    public String newEventSubmit(@CurrentUser Account account, @PathVariable("path") String path, Model model,
                                 @ModelAttribute("eventForm") @Valid EventForm eventForm, BindingResult bindingResult) {
        Study study = studyService.getStudyToUpdate(path, account);
        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute("study", EventStudyInfo.create(study));
            return "event/form";
        }

        Event event = eventService.createEvent(eventForm.createEvent(study, account));
        return String.format("redirect:/study/%s/events/%s", URLEncoder.encode(path, UTF_8), event.getId());
    }

    @GetMapping("/events/{id}")
    public String getEvent(@CurrentUser Account account, @PathVariable("path") String path, @PathVariable("id") Long id, Model model) {
        Study study = studyRepository.findStudyWithManagerByPath(path).orElseThrow(() -> new IllegalStateException("존재하지 않는 스터디 입니다."));
        Event event = eventRepository.findWithEnrollmentById(id).orElseThrow(() -> new IllegalStateException("존재하지 않는 모임 입니다."));
        model.addAttribute(account);
        model.addAttribute("study", EventStudyQueryForm.create(study, account));
        model.addAttribute("event", EventQueryForm.create(event, account));

        return "event/view";
    }

    @GetMapping("/events")
    public String viewStudyEvents(@CurrentUser Account account, @PathVariable("path") String path, Model model) {
        Study study = studyRepository.findAllByPath(path).orElseThrow(() -> new IllegalStateException("존재하지 않는 스터디 입니다."));
        model.addAttribute(account);
        model.addAttribute("study", StudyQueryForm.createForm(study, account));

        List<Event> events = eventRepository.findByStudyOrderByStartDateTime(study);
        List<Event> oldEvents = new ArrayList<>();
        List<Event> newEvents = new ArrayList<>();

        events.forEach(e -> {
            if (e.getEndDateTime().isBefore(LocalDateTime.now())) {
                oldEvents.add(e);
            } else {
                newEvents.add(e);
            }
        });

        model.addAttribute("newEvents", newEvents.stream().map(EventEventsForm::create).toList());
        model.addAttribute("oldEvents", oldEvents.stream().map(EventEventsForm::create).toList());

        return "study/events";
    }

    @GetMapping("/events/{id}/edit")
    public String updateEventForm(@CurrentUser Account account, Model model,
                                  @PathVariable("path") String path, @PathVariable("id") Long id) {
        Study study = studyService.getStudyToUpdate(path, account);
        Event event = eventRepository.findById(id).orElseThrow(() -> new IllegalStateException("존재하지 않는 모임 입니다."));
        model.addAttribute(account);
        model.addAttribute("event", EventEventEditForm.builder()
                        .id(event.getId())
                        .title(event.getTitle())
                        .build());
        model.addAttribute("study", EventStudyInfo.create(study));
        model.addAttribute("eventEditForm", EventEditForm.create(event));

        return "event/update-form";
    }

    @PostMapping("/events/{id}/edit")
    public String updateEvent(@CurrentUser Account account, @PathVariable("path") String path, @PathVariable("id") Long id, Model model,
                              @ModelAttribute("eventEditForm") @Valid EventEditForm eventEditForm, BindingResult bindingResult) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new IllegalStateException("존재하지 않는 모임 입니다."));
        if (bindingResult.hasErrors()) {
            Study study = studyService.getStudyToUpdate(path, account);
            model.addAttribute(account);
            model.addAttribute("event", EventEventEditForm.builder()
                    .id(event.getId())
                    .title(event.getTitle())
                    .build());
            model.addAttribute("study", EventStudyInfo.create(study));
            return "event/update-form";
        }

        eventService.updateEvent(id, eventEditForm.createEvent());
        return String.format("redirect:/study/%s/events/%s", URLEncoder.encode(path, UTF_8), event.getId());
    }

    @PostMapping("/events/{id}/delete")
    public String cancelEvent(@CurrentUser Account account, @PathVariable("path") String path, @PathVariable("id") Long id) {
        Study study = studyService.getStudyToUpdate(path, account);
        eventService.cancelEvent(id);

        return String.format("redirect:/study/%s/events", URLEncoder.encode(path, UTF_8));
    }

    @PostMapping("/events/{id}/enroll")
    public String newEnrollment(@CurrentUser Account account, @PathVariable("path") String path, @PathVariable("id") Long id) {
        studyRepository.findByPath(path).orElseThrow(() -> new IllegalStateException("존재하지 않는 스터디 입니다."));

        eventService.newEnrollment(account, id);
        return String.format("redirect:/study/%s/events/%s", URLEncoder.encode(path, UTF_8), id);
    }

    @PostMapping("/events/{id}/disenroll")
    public String cancelEnrollment(@CurrentUser Account account, @PathVariable("path") String path, @PathVariable("id") Long id) {
        studyRepository.findByPath(path).orElseThrow(() -> new IllegalStateException("존재하지 않는 스터디 입니다."));

        eventService.cancelEnrollment(account, id);
        return String.format("redirect:/study/%s/events/%s", URLEncoder.encode(path, UTF_8), id);
    }

    @PostMapping("/events/{eventId}/enrollments/{enrollmentId}/accept")
    public String acceptEnrollment(@CurrentUser Account account, @PathVariable("path") String path,
                                   @PathVariable("eventId") Long eventId, @PathVariable("enrollmentId") Long enrollmentId) {
        studyRepository.findByPath(path).orElseThrow(() -> new IllegalStateException("존재하지 않는 스터디 입니다."));
        eventService.acceptEnrollment(eventId, enrollmentId, account);

        return String.format("redirect:/study/%s/events/%s", URLEncoder.encode(path, UTF_8), eventId);
    }

    @PostMapping("/events/{eventId}/enrollments/{enrollmentId}/reject")
    public String rejectEnrollment(@CurrentUser Account account, @PathVariable("path") String path,
                                   @PathVariable("eventId") Long eventId, @PathVariable("enrollmentId") Long enrollmentId) {
        studyRepository.findByPath(path).orElseThrow(() -> new IllegalStateException("존재하지 않는 스터디 입니다."));

        eventService.rejectEnrollment(eventId, enrollmentId, account);

        return String.format("redirect:/study/%s/events/%s", URLEncoder.encode(path, UTF_8), eventId);
    }

    @PostMapping("/events/{eventId}/enrollments/{enrollmentId}/checkIn")
    public String checkInEnrollment(@CurrentUser Account account, @PathVariable("path") String path,
                                    @PathVariable("eventId") Long eventId, @PathVariable("enrollmentId") Long enrollmentId) {
        studyRepository.findByPath(path).orElseThrow(() -> new IllegalStateException("존재하지 않는 스터디 입니다."));
        eventService.checkInEnrollment(eventId, enrollmentId, account);
        return String.format("redirect:/study/%s/events/%s", URLEncoder.encode(path, UTF_8), eventId);
    }

    @PostMapping("/events/{eventId}/enrollments/{enrollmentId}/cancel-checkIn")
    public String cancelCheckInEnrollment(@CurrentUser Account account, @PathVariable("path") String path,
                                    @PathVariable("eventId") Long eventId, @PathVariable("enrollmentId") Long enrollmentId) {
        studyRepository.findByPath(path).orElseThrow(() -> new IllegalStateException("존재하지 않는 스터디 입니다."));
        eventService.cancelCheckInEnrollment(eventId, enrollmentId, account);
        return String.format("redirect:/study/%s/events/%s", URLEncoder.encode(path, UTF_8), eventId);
    }

}


