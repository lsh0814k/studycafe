package com.studycafe.modules.event.web;

import com.studycafe.infra.MockMvcTest;
import com.studycafe.modules.account.AccountFactory;
import com.studycafe.modules.account.annotation.WithAccount;
import com.studycafe.modules.account.domain.Account;
import com.studycafe.modules.account.repository.AccountRepository;
import com.studycafe.modules.event.domain.Enrollment;
import com.studycafe.modules.event.domain.Event;
import com.studycafe.modules.event.domain.EventType;
import com.studycafe.modules.event.repository.EventRepository;
import com.studycafe.modules.event.service.EventService;
import com.studycafe.modules.study.StudyFactory;
import com.studycafe.modules.study.domain.Study;
import com.studycafe.modules.study.repository.StudyRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URLEncoder;
import java.time.LocalDateTime;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class EventControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private AccountRepository accountRepository;
    @Autowired private EventService eventService;
    @Autowired private EventRepository eventRepository;
    @Autowired private StudyRepository studyRepository;
    @Autowired private AccountFactory accountFactory;
    @Autowired private StudyFactory studyFactory;
    @Autowired private EventFactory eventFactory;

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
        Study study = studyFactory.createStudy(account);
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
        Study study = studyFactory.createStudy(account);

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
        Study study = studyFactory.createStudy(account);
        Event event = eventFactory.createEvent(account, study, EventType.FCFS, 2);

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
        Study study = studyFactory.createStudy(account);
        Event event = eventFactory.createEvent(account, study, EventType.FCFS, 2);

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
        Study study = studyFactory.createStudy(account);
        Event event = eventFactory.createEvent(account, study, EventType.FCFS, 2);

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
        Study study = studyFactory.createStudy(account);
        Event event = eventFactory.createEvent(account, study, EventType.FCFS, 2);

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
        Study study = studyFactory.createStudy(account);
        Event event = eventFactory.createEvent(account, study, EventType.FCFS, 2);

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
        Account account = accountFactory.createAccount("name");
        Study study = studyFactory.createStudy(account);
        Event event = eventFactory.createEvent(account, study, EventType.FCFS, 2);

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
        Account account = accountFactory.createAccount("name");
        Study study = studyFactory.createStudy(account);
        Event event = eventFactory.createEvent(account, study, EventType.FCFS, 2);

        eventService.newEnrollment(account, event.getId());
        eventService.newEnrollment(accountFactory.createAccount("name2"), event.getId());

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
        Account name = accountFactory.createAccount("name");
        Study study = studyFactory.createStudy(name);
        Event event = eventFactory.createEvent(name, study, EventType.FCFS, 2);

        Account nick = accountRepository.findByNickname("nick").get();
        Account name2 = accountFactory.createAccount("name2");
        Account name3 = accountFactory.createAccount("name3");

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
        Account name = accountFactory.createAccount("name");
        Study study = studyFactory.createStudy(name);
        Event event = eventFactory.createEvent(name, study, EventType.CONFIRMATIVE, 2);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/study/%s/events/%s", URLEncoder.encode(study.getPath(), UTF_8), event.getId())));

        Event findEvent = eventRepository.findWithEnrollmentById(event.getId()).get();
        isEnrollNotAccepted(findEvent, accountRepository.findByNickname("nick").get());
    }

    @Test
    @DisplayName("관리자 확인 모임에 참가 신청 수락")
    @WithAccount("nick")
    void enrollment_to_CONFIRMATIVE_accepted() throws Exception {
        Account account = accountFactory.createAccount("name");
        Study study = studyFactory.createStudy(account);
        Event event = eventFactory.createEvent(account, study, EventType.CONFIRMATIVE, 2);
        eventService.newEnrollment(account, event.getId());
        Enrollment enrollment = eventRepository.findWithEnrollmentById(event.getId()).get().getEnrollments().get(0);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enrollments/" + enrollment.getId() + "/accept")
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/study/%s/events/%s", URLEncoder.encode(study.getPath(), UTF_8), event.getId())));

        Event findEvent = eventRepository.findWithEnrollmentById(event.getId()).get();
        isEnrollAccepted(findEvent, account);
    }

    @Test
    @DisplayName("관리자 확인 모임에 참가 신청 거절")
    @WithAccount("nick")
    void enrollment_to_CONFIRMATIVE_reject() throws Exception {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = studyFactory.createStudy(account);
        Event event = eventFactory.createEvent(account, study, EventType.CONFIRMATIVE, 2);
        eventService.newEnrollment(account, event.getId());
        Enrollment enrollment = eventRepository.findWithEnrollmentById(event.getId()).get().getEnrollments().get(0);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enrollments/" + enrollment.getId() + "/reject")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/study/%s/events/%s", URLEncoder.encode(study.getPath(), UTF_8), event.getId())));

        Event findEvent = eventRepository.findWithEnrollmentById(event.getId()).get();
        isEnrollNotAccepted(findEvent, account);
    }

    @Test
    @DisplayName("모임 체크인")
    @WithAccount("nick")
    void enrollment_checkIn() throws Exception {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = studyFactory.createStudy(account);
        Event event = eventFactory.createEvent(account, study, EventType.FCFS, 2);
        eventService.newEnrollment(account, event.getId());
        Enrollment enrollment = eventRepository.findWithEnrollmentById(event.getId()).get().getEnrollments().get(0);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enrollments/" + enrollment.getId() + "/checkIn")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/study/%s/events/%s", URLEncoder.encode(study.getPath(), UTF_8), event.getId())));

        isEnrollCheckIn(account, event);
    }

    private void isEnrollCheckIn(Account account, Event event) {
        Event findEvent = eventRepository.findWithEnrollmentById(event.getId()).get();
        assertTrue(findEvent.getEnrollments().stream()
                .filter(e -> e.getAccount().equals(account))
                .findFirst()
                .get()
                .isAttended());
    }

    @Test
    @DisplayName("모임 체크인 취소")
    @WithAccount("nick")
    void enrollment_cancel_checkIn() throws Exception {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = studyFactory.createStudy(account);
        Event event = eventFactory.createEvent(account, study, EventType.FCFS, 2);
        eventService.newEnrollment(account, event.getId());
        Enrollment enrollment = eventRepository.findWithEnrollmentById(event.getId()).get().getEnrollments().get(0);
        eventService.checkInEnrollment(event.getId(), enrollment.getId(), account);
        isEnrollCheckIn(account, event);


        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enrollments/" + enrollment.getId() + "/cancel-checkIn")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/study/%s/events/%s", URLEncoder.encode(study.getPath(), UTF_8), event.getId())));

        Event findEvent = eventRepository.findWithEnrollmentById(event.getId()).get();
        assertFalse(findEvent.getEnrollments().stream()
                .filter(e -> e.getAccount().equals(account))
                .findFirst()
                .get()
                .isAttended());
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
}