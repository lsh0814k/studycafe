package com.studcafe.event.web;

import com.studcafe.account.domain.Account;
import com.studcafe.account.repository.AccountRepository;
import com.studcafe.account.service.AccountService;
import com.studcafe.account.web.dto.SignUpForm;
import com.studcafe.event.domain.Enrollment;
import com.studcafe.event.domain.Event;
import com.studcafe.event.domain.EventType;
import com.studcafe.event.repository.EventRepository;
import com.studcafe.event.service.EventService;
import com.studcafe.security.annotation.WithAccount;
import com.studcafe.study.domain.Study;
import com.studcafe.study.repository.StudyRepository;
import com.studcafe.study.service.StudyService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URLEncoder;
import java.time.LocalDateTime;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;
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
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AccountService accountService;

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
        Account account = accountRepository.findByNickname("nick").get();
        Study study = createStudy(account);
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
        Account account = accountRepository.findByNickname("nick").get();
        Study study = createStudy(account);

        mockMvc.perform(post("/study/" + study.getPath() + "/new-event")
                        .param("title", "title")
                        .param("description", "description")
                        .param("eventType", EventType.FCFS.toString())
                        .param("endEnrollmentDateTime", LocalDateTime.now().plusHours(1).toString())
                        .param("startDateTime", LocalDateTime.now().plusHours(2).toString())
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
        Event event = createEvent(account, study, EventType.FCFS, 2);

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
        Event event = createEvent(account, study, EventType.FCFS, 2);

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
        Event event = createEvent(account, study, EventType.FCFS, 2);

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
        Event event = createEvent(account, study, EventType.FCFS, 2);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/edit")
                .with(csrf())
                .param("id", event.getId().toString())
                .param("title", "title")
                .param("description", "description")
                .param("eventType", EventType.FCFS.toString())
                .param("endEnrollmentDateTime", LocalDateTime.now().plusHours(1).toString())
                .param("startDateTime", LocalDateTime.now().plusHours(2).toString())
                .param("endDateTime", LocalDateTime.now().plusDays(2).toString())
                .param("limitOfEnrollments", String.valueOf(3))
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/study/%s/events/%s", study.getPath(), event.getId())));

        Event findEvent = eventRepository.findById(event.getId()).get();
        assertEquals(3, findEvent.getLimitOfEnrollments());
    }

    @Test
    @DisplayName("모임 취소")
    @WithAccount("nick")
    void event_cancel() throws Exception {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = createStudy(account);
        Event event = createEvent(account, study, EventType.FCFS, 2);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/delete")
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/study/%s/events", study.getPath())));

        assertTrue(eventRepository.findById(event.getId()).isEmpty());
    }



    @Test
    @DisplayName("선착순 모임에 참가 신청 - 자동 수락")
    @WithAccount("nick")
    void newEnrollment_to_FCFS_event_accepted() throws Exception {
        Account account = createAccount("name");
        Study study = createStudy(account);
        Event event = createEvent(account, study, EventType.FCFS, 2);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/study/%s/events/%s", URLEncoder.encode(study.getPath(), UTF_8), event.getId())));

        Event findEvent = eventRepository.findWithEnrollmentById(event.getId()).get();
        assertEquals(1, findEvent.getEnrollments().stream().filter(Enrollment::isAccepted).count());
    }

    @Test
    @DisplayName("선착순 모임에 참가 신청 - 대기중 (이미 인원이 꽉 차서")
    @WithAccount("nick")
    void newEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account account = createAccount("name");
        Study study = createStudy(account);
        Event event = createEvent(account, study, EventType.FCFS, 2);

        eventService.newEnrollment(account, event.getId());
        eventService.newEnrollment(createAccount("name2"), event.getId());

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/study/%s/events/%s", URLEncoder.encode(study.getPath(), UTF_8), event.getId())));

        Event findEvent = eventRepository.findWithEnrollmentById(event.getId()).get();
        Account nick = accountRepository.findByNickname("nick").get();
        assertFalse(findEvent.getEnrollments().stream()
                .filter(e -> e.getAccount().equals(nick))
                .findFirst()
                .get()
                .isAccepted());
    }

    @Test
    @DisplayName("선착순 모임에 참가 신청 - 취소")
    @WithAccount("nick")
    void cancelEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account name = createAccount("name");
        Study study = createStudy(name);
        Event event = createEvent(name, study, EventType.FCFS, 2);

        Account nick = accountRepository.findByNickname("nick").get();
        Account name2 = createAccount("name2");
        Account name3 = createAccount("name3");

        eventService.newEnrollment(nick, event.getId());
        eventService.newEnrollment(name2, event.getId());
        eventService.newEnrollment(name3, event.getId());

        Event findEvent = eventRepository.findWithEnrollmentById(event.getId()).get();
        isEnrollAccepted(findEvent, nick);
        isEnrollAccepted(findEvent, name2);
        isEnrollNotAccepted(findEvent, name3);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/disenroll")
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/study/%s/events/%s", URLEncoder.encode(study.getPath(), UTF_8), event.getId())));

        Event eventEnroll = eventRepository.findWithEnrollmentById(event.getId()).get();
        assertEquals(2, eventEnroll.getEnrollments().size());
        assertEquals(2, eventEnroll.getEnrollments().stream().filter(Enrollment::isAccepted).count());
    }

    @Test
    @DisplayName("관리자 확인 모임에 참가 신청 - 대기중")
    @WithAccount("nick")
    void newEnrollment_to_CONFIRMATIVE_not_accepted() throws Exception {
        Account name = createAccount("name");
        Study study = createStudy(name);
        Event event = createEvent(name, study, EventType.CONFIRMATIVE, 2);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/study/%s/events/%s", URLEncoder.encode(study.getPath(), UTF_8), event.getId())));

        Event findEvent = eventRepository.findWithEnrollmentById(event.getId()).get();
        isEnrollNotAccepted(findEvent, accountRepository.findByNickname("nick").get());
    }

    private void isEnrollAccepted(Event event, Account account) {
        assertTrue(event.getEnrollments().stream()
                .filter(e -> e.getAccount().equals(account))
                .findFirst()
                .get()
                .isAccepted());
    }

    private void isEnrollNotAccepted(Event event, Account account) {
        assertFalse(event.getEnrollments().stream()
                .filter(e -> e.getAccount().equals(account))
                .findFirst()
                .get()
                .isAccepted());
    }

    private Event createEvent(Account account, Study study, EventType eventType, int limit) {
        Event event = Event.builder()
                .title("모임")
                .description("description")
                .eventType(eventType)
                .endEnrollmentDateTime(LocalDateTime.now())
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(2))
                .createDateTime(LocalDateTime.now())
                .study(study)
                .createdBy(account)
                .limitOfEnrollments(limit)
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

    private Account createAccount(String nickname) {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail(nickname + "@email.com");
        signUpForm.setNickname(nickname);
        signUpForm.setPassword("123456789");
        Account account = signUpForm.createAccount(passwordEncoder);
        accountService.processNewAccount(account);

        return account;
    }
}