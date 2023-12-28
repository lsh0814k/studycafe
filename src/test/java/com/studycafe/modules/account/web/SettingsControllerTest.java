package com.studycafe.modules.account.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studycafe.infra.MockMvcTest;
import com.studycafe.modules.account.annotation.WithAccount;
import com.studycafe.modules.account.domain.Account;
import com.studycafe.modules.account.repository.AccountRepository;
import com.studycafe.modules.account.service.AccountService;
import com.studycafe.modules.account.web.dto.TagForm;
import com.studycafe.modules.account.web.dto.ZoneForm;
import com.studycafe.modules.tag.domain.Tag;
import com.studycafe.modules.tag.repository.TagRepository;
import com.studycafe.modules.zone.domain.Zone;
import com.studycafe.modules.zone.repository.ZoneRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    ZoneRepository zoneRepository;

    @Autowired
    ObjectMapper objectMapper;

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
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("Spring");
        mockMvc.perform(post("/settings/tags/add")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
        )
                .andExpect(status().isOk());


        Tag tag = tagRepository.findByTitle("Spring").get();
        assertEquals("Spring", tag.getTitle());

        Account account = accountRepository.findWithTagsByEmail("nick@email.com").get();
        assertTrue(account.getTags().stream().map(Tag::getTitle).toList().contains("Spring"));
    }

    @WithAccount("nick")
    @DisplayName("관심 주제 조회")
    @Test
    void tags_search() throws Exception{
        accountService.addTag("nick@email.com", "Srping");

        mockMvc.perform(get(SettingsController.SETTINGS_TAGS_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("tags"))
                .andExpect(view().name(SettingsController.SETTINGS_TAGS_VIEW_NAME));

    }

    @WithAccount("nick")
    @DisplayName("관심 주제 삭제")
    @Test
    void tags_remove() throws Exception {
        accountService.addTag("nick@email.com", "Spring");
        accountService.addTag("nick@email.com", "JPA");
        accountService.addTag("nick@email.com", "Java");

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("Spring");
        mockMvc.perform(post("/settings/tags/remove")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
        )
                .andExpect(status().isOk());

        Optional<Tag> tagOptional = tagRepository.findByTitle("Spring");
        assertTrue(tagOptional.isPresent());

        Account account = accountRepository.findWithTagsByEmail("nick@email.com").get();
        assertFalse(account.getTags().stream().map(Tag::getTitle).toList().contains("Spring"));
    }

    @WithAccount("nick")
    @DisplayName("관심 주제 폼")
    @Test
    void tags_form() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_TAGS_URL))
                .andExpect(view().name(SettingsController.SETTINGS_TAGS_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));
    }

    @WithAccount("nick")
    @DisplayName("활동 지역 폼")
    @Test
    void zones_form() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_ZONES_URL))
                .andExpect(view().name(SettingsController.SETTINGS_ZONES_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("zones"));
    }

    @WithAccount("nick")
    @DisplayName("활동 지역 저장")
    @Test
    void zones_add() throws Exception {
        Zone zone = zoneRepository.findByCityAndProvince("Ansan", "Gyeonggi").get();
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(zone.toString());

        mockMvc.perform(post(SettingsController.SETTINGS_ZONES_URL + "/add")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm))
        )
                .andExpect(status().isOk());

        Account account = accountRepository.findWithZonesByEmail("nick@email.com").get();
        assertEquals(1, account.getZones().size());
        assertTrue(account.getZones().contains(zone));
    }

    @WithAccount("nick")
    @DisplayName("활동 지역 삭제")
    @Test
    void zone_remove() throws Exception {
        Zone zone = zoneRepository.findByCityAndProvince("Ansan", "Gyeonggi").get();
        accountService.addZone("nick@email.com", zone);
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(zone.toString());


        mockMvc.perform(post(SettingsController.SETTINGS_ZONES_URL + "/remove")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm))
        )
                .andExpect(status().isOk());

        Account account = accountRepository.findWithZonesByEmail("nick@email.com").get();
        assertEquals(0, account.getZones().size());
        assertFalse(account.getZones().contains(zone));
    }
}