package com.studcafe.event.web;

import com.studcafe.account.domain.Account;
import com.studcafe.account.repository.AccountRepository;
import com.studcafe.event.domain.Event;
import com.studcafe.event.domain.EventType;
import com.studcafe.event.repository.EventRepository;
import com.studcafe.event.service.EventService;
import com.studcafe.security.annotation.WithAccount;
import com.studcafe.study.domain.Study;
import com.studcafe.study.repository.StudyRepository;
import com.studcafe.study.service.StudyService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private AccountRepository accountRepository;
    @Autowired private StudyService studyService;
    @Autowired private EventService eventService;
    @Autowired private EventRepository eventRepository;
    @Autowired private StudyRepository studyRepository;

    @AfterEach
    public void afterEach() {
        eventRepository.deleteAll();
        studyRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("모임 생성 form")
    @WithAccount("nick")
    void event_form() throws Exception {
        Study study = createStudy();
        mockMvc.perform(get("/study/" + study.getPath() + "/new-event"))
                .andExpect(view().name("event/form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("eventForm"));
    }

    @Test
    @DisplayName("모임 생성")
    @WithAccount("nick")
    void event() throws Exception {
        Study study = createStudy();

        mockMvc.perform(post("/study/" + study.getPath() + "/new-event")
                        .param("title", "title")
                        .param("description", "description")
                        .param("eventType", EventType.FCFS.toString())
                        .param("endEnrollmentDateTime", LocalDateTime.now().toString())
                        .param("startDateTime", LocalDateTime.now().toString())
                        .param("endDateTime", LocalDateTime.now().plusDays(2).toString())
                        .param("limitOfEnrollments", String.valueOf(2))
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("모임 상세 화면")
    @WithAccount("nick")
    void event_view() throws Exception {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = createStudy(account);
        Event event = createEvent(account, study);

        mockMvc.perform(get("/study/" + study.getPath() + "/events/" + event.getId()))
                .andExpect(view().name("event/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("event"));
    }

    @Test
    @DisplayName("모임 목록 조회")
    @WithAccount("nick")
    void event_query() throws Exception {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = createStudy(account);
        Event event = createEvent(account, study);

        mockMvc.perform(get("/study/" + study.getPath() + "/events"))
                .andExpect(view().name("study/events"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("oldEvents"))
                .andExpect(model().attributeExists("newEvents"));
    }

    @Test
    @DisplayName("모임 수정 폼")
    @WithAccount("nick")
    void event_edit_form() throws Exception {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = createStudy(account);
        Event event = createEvent(account, study);

        mockMvc.perform(get("/study/" + study.getPath() + "/events/" + event.getId() + "/edit"))
                .andExpect(view().name("event/update-form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("eventEditForm"));
    }

    @Test
    @DisplayName("모임 수정")
    @WithAccount("nick")
    void event_edit() throws Exception {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = createStudy(account);
        Event event = createEvent(account, study);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/edit")
                .with(csrf())
                .param("id", event.getId().toString())
                .param("title", "title")
                .param("description", "description")
                .param("eventType", EventType.FCFS.toString())
                .param("endEnrollmentDateTime", LocalDateTime.now().minusHours(1).toString())
                .param("startDateTime", LocalDateTime.now().toString())
                .param("endDateTime", LocalDateTime.now().plusDays(2).toString())
                .param("limitOfEnrollments", String.valueOf(3))
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/study/%s/events/%s", study.getPath(), event.getId())));
    }

    private Event createEvent(Account account, Study study) {
        Event event = Event.builder()
                .title("모임")
                .description("description")
                .eventType(EventType.FCFS)
                .endEnrollmentDateTime(LocalDateTime.now())
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(2))
                .createDateTime(LocalDateTime.now())
                .build();

        eventService.createEvent(event);
        return event;
    }

    private Study createStudy(Account account) {
        Study study = Study.builder()
                .path("test-path")
                .title("study title")
                .fullDescription("short description of a study")
                .shortDescription("full description of a study")
                .build();
        studyService.createNewStudy(account, study);

        return study;
    }

    private Study createStudy() {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = Study.builder()
                .path("test-path")
                .title("study title")
                .fullDescription("short description of a study")
                .shortDescription("full description of a study")
                .build();
        studyService.createNewStudy(account, study);

        return study;
    }
}