package com.studcafe.study.web;

import com.studcafe.account.domain.Account;
import com.studcafe.account.repository.AccountRepository;
import com.studcafe.account.service.AccountService;
import com.studcafe.account.web.dto.SignUpForm;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StudyControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private StudyService studyService;
    @Autowired private StudyRepository studyRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private AccountService accountService;
    @Autowired private PasswordEncoder passwordEncoder;

    @AfterEach
    void beforeEach() {
        studyRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("스터디 개설 폼 조회")
    @WithAccount("nick")
    void createStudyForm() throws Exception {
        mockMvc.perform(get("/new-study"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("스터디 개설 - 완료")
    @WithAccount("nick")
    void createStudy_success() throws Exception {
        mockMvc.perform(post("/new-study")
                .with(csrf())
                .param("path", "test-path")
                .param("title", "study title")
                .param("shortDescription", "short description of a study")
                .param("fullDescription", "full description of a study")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/test-path"));

        Study study = studyRepository.findAllByPath("test-path").get();
        assertNotNull(study);
        Account account = accountRepository.findByNickname("nick").get();
        assertTrue(study.getManagers().stream().filter(d -> d.getAccount().equals(account)).count() == 1);
    }

    @Test
    @DisplayName("스터디 개설 - 실패")
    @WithAccount("nick")
    void createStudy_fail() throws Exception {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = createStudy(account);

        mockMvc.perform(post("/new-study")
                .with(csrf())
                .param("path", "test-path")
                .param("title", "study title")
                .param("shortDescription", "short description of a study")
                .param("fullDescription", "full description of a study")
        )
                .andExpect(status().isOk())
                .andExpect(view().name("study/form"))
                .andExpect(model().attributeHasErrors("studyForm"))
                .andExpect(model().attributeHasFieldErrorCode("studyForm", "path", "wrong.path"));
    }

    @Test
    @DisplayName("스터디 조회")
    @WithAccount("nick")
    void view_study() throws Exception {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = createStudy(account);

        mockMvc.perform(get("/study/test-path"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/view"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("account"));
    }

    @Test
    @DisplayName("스터디 manager 조회")
    @WithAccount("nick")
    void view_manager() throws Exception {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = createStudy(account);

        mockMvc.perform(get("/study/test-path"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/view"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("account"));
    }

    @Test
    @DisplayName("스터디 멤버 등록")
    @WithAccount("nick")
    void member_add() throws Exception {
        Study study = createStudy(createAccount());

        mockMvc.perform(post("/study/" + study.getPath() + "/join")
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/members"));

        Study findStudy = studyRepository.getStudyToUpdateMember(study.getPath()).get();
        Account account = accountRepository.findByNickname("nick").get();
        assertTrue(findStudy.getMembers().stream().filter(sm -> sm.getAccount().equals(account)).count() == 1);
    }

    @Test
    @DisplayName("스터디 멤버 탈퇴")
    @WithAccount("nick")
    void member_leave() throws Exception {
        Study study = createStudy(createAccount());
        Account account = accountRepository.findByNickname("nick").get();
        studyService.addMember(study.getPath(), account);

        mockMvc.perform(post("/study/" + study.getPath() + "/leave")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/members"));

        Study findStudy = studyRepository.getStudyToUpdateMember(study.getPath()).get();
        assertTrue(findStudy.getMembers().stream().filter(sm -> sm.getAccount().equals(account)).count() == 0);
    }

    private Account createAccount() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail("name@email.com");
        signUpForm.setNickname("name");
        signUpForm.setPassword("123456789");
        Account account = signUpForm.createAccount(passwordEncoder);
        accountService.processNewAccount(account);

        return account;
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
}