package com.studcafe.account.web;

import com.studcafe.account.domain.Account;
import com.studcafe.account.domain.Tag;
import com.studcafe.account.repository.AccountRepository;
import com.studcafe.security.annotation.WithAccount;
import com.studcafe.tag.repository.TagRepository;
import lombok.With;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TagRepository tagRepository;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @WithAccount("nick")
    @DisplayName("프로필 수정 폼")
    @Test
    void updateProfileForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().attributeExists("account"));
    }

    @WithAccount("nick")
    @DisplayName("프로필 수정하기 - 입력 값 정상")
    @Test
    void updateProfile() throws Exception {
        String bio = "짧은 소개를 수정";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname("nick").get();
        assertEquals(bio, account.getBio());
    }


    @WithAccount("nick")
    @DisplayName("프로필 수정하기 - 입력 값 에러")
    @Test
    void updateProfile_error() throws Exception {
        String bio = "긴 소개글 오류 발생긴 소개글 오류 발생긴 소개글 오류 발생긴 소개글 오류 발생긴 소개글 오류 발생긴 소개글 오류 발생긴 소개글 오류 발생 오류 발생";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                        .param("bio", bio)
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().hasErrors());

        Account account = accountRepository.findByNickname("nick").get();
        assertNotEquals(bio, account.getBio());
    }

    @WithAccount("nick")
    @DisplayName("패스워드 수정 폼")
    @Test
    void updatePassword_form() throws Exception{
        mockMvc.perform(get(SettingsController.SETTINGS_PASSWORD_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithAccount("nick")
    @DisplayName("패스워드 수정 - 입력 값 정상")
    @Test
    void updatePassword_success() throws Exception {
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                .with(csrf())
                .param("newPassword", "12345678")
                .param("newPasswordConfirm", "12345678")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PASSWORD_VIEW_NAME))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname("nick").get();
        assertTrue(passwordEncoder.matches("12345678", account.getPassword()));
    }

    @WithAccount("nick")
    @DisplayName("패스워드 수정 - 입력 값 에러 - 패스워드 불일치")
    @Test
    void updatePassword_fail() throws Exception {
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                        .with(csrf())
                        .param("newPassword", "12345678")
                        .param("newPasswordConfirm", "11111111")
                )
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PASSWORD_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithAccount("nick")
    @DisplayName("알림 수정 폼")
    @Test
    void notifications_form() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_NOTIFICATIONS_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("notifications"));
    }

    @WithAccount("nick")
    @DisplayName("알림 수정")
    @Test
    void notifications() throws Exception {
        mockMvc.perform(post(SettingsController.SETTINGS_NOTIFICATIONS_URL)
                        .with(csrf())
                        .param("studyCreatedByEmail", "true")
                        .param("studyCreatedByWeb", "true")
                        .param("studyEnrollmentResultByEmail", "true")
                        .param("studyEnrollmentResultByWeb", "true")
                        .param("studyUpdatedByEmail", "true")
                        .param("studyUpdatedByWeb", "true")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_NOTIFICATIONS_URL))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname("nick").get();
        assertTrue(account.isStudyCreatedByEmail());
    }

    @WithAccount("nick")
    @DisplayName("관심 주제 등록")
    @Test
    void tags_save() throws Exception{
        String requestJson = "{\"tagTitle\": \"Spring\"}";
        mockMvc.perform(post("/settings/tags/add")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        )
                .andExpect(status().isOk());


        Tag tag = tagRepository.findByTitle("Spring").get();
        assertEquals("Spring", tag.getTitle());

        Account account = accountRepository.findByNickname("nick").get();
    }

    @WithAccount("nick")
    @DisplayName("관심 주제 조회")
    @Test
    void tags_search() throws Exception{
        mockMvc.perform(get(SettingsController.SETTINGS_TAGS_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("tags"))
                .andExpect(view().name(SettingsController.SETTINGS_TAGS_VIEW_NAME));

    }
}