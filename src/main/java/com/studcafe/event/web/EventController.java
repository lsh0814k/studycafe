package com.studcafe.event.web;

import com.studcafe.account.domain.Account;
import com.studcafe.event.domain.Event;
import com.studcafe.event.repository.EventRepository;
import com.studcafe.event.service.EventService;
import com.studcafe.event.web.dto.EventForm;
import com.studcafe.event.web.dto.EventQueryForm;
import com.studcafe.event.web.dto.EventStudyInfo;
import com.studcafe.event.web.dto.EventStudyQueryForm;
import com.studcafe.event.web.validator.EventFormValidator;
import com.studcafe.main.annotation.CurrentUser;
import com.studcafe.study.domain.Study;
import com.studcafe.study.repository.StudyRepository;
import com.studcafe.study.service.StudyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;

import static java.nio.charset.StandardCharsets.UTF_8;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study/{path}")
public class EventController {

    private final StudyService studyService;
    private final EventService eventService;
    private final StudyRepository studyRepository;
    private final EventRepository eventRepository;

    @InitBinder("eventForm")
    public void initBinderEventForm(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new EventFormValidator());
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
}
