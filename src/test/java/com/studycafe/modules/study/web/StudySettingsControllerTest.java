package com.studycafe.modules.study.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studycafe.infra.MockMvcTest;
import com.studycafe.modules.account.annotation.WithAccount;
import com.studycafe.modules.account.domain.Account;
import com.studycafe.modules.account.repository.AccountRepository;
import com.studycafe.modules.account.web.dto.TagForm;
import com.studycafe.modules.account.web.dto.ZoneForm;
import com.studycafe.modules.study.StudyFactory;
import com.studycafe.modules.study.domain.Study;
import com.studycafe.modules.study.repository.StudyRepository;
import com.studycafe.modules.study.service.StudyService;
import com.studycafe.modules.zone.domain.Zone;
import com.studycafe.modules.zone.repository.ZoneRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class StudySettingsControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private StudyRepository studyRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private StudyService studyService;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ZoneRepository zoneRepository;
    @Autowired private StudyFactory studyFactory;

    @AfterEach
    void afterEach() {
        studyRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("스터디 description 수정 form")
    @WithAccount("nick")
    void description_form() throws Exception {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = studyFactory.createStudy(account);

        mockMvc.perform(get("/study/" + study.getPath() + "/settings/description"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("studyDescriptionForm"))
                .andExpect(view().name("study/settings/description"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("스터디 description 수정")
    @WithAccount("nick")
    void description_success() throws Exception {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = studyFactory.createStudy(account);

        mockMvc.perform(post("/study/"+ study.getPath() +"/settings/description")
                .param("fullDescription", "update full description of a study")
                .param("shortDescription", "update short description of a study")
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl("/study/test-path/settings/description"));

        Study findStudy = studyRepository.findByPath("test-path").get();
        assertTrue(findStudy.getFullDescription().equals("update full description of a study"));
        assertTrue(findStudy.getShortDescription().equals("update short description of a study"));
    }



    @Test
    @DisplayName("스터디 banner form")
    @WithAccount("nick")
    void banner_form() throws Exception {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = studyFactory.createStudy(account);
        mockMvc.perform(get("/study/" + study.getPath() + "/settings/banner"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }

    @Test
    @DisplayName("스터디 banner enable")
    @WithAccount("nick")
    void banner_enable() throws Exception {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = studyFactory.createStudy(account);
        mockMvc.perform(post("/study/" + study.getPath() + "/settings/banner/enable")
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/settings/banner"));

        Study findStudy = studyRepository.findByPath("test-path").get();
        assertTrue(findStudy.isUseBanner());
    }

    @Test
    @DisplayName("스터디 banner disable")
    @WithAccount("nick")
    void banner_disable() throws Exception {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = studyFactory.createStudy(account);
        mockMvc.perform(post("/study/" + study.getPath() + "/settings/banner/disable")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/settings/banner"));

        Study findStudy = studyRepository.findByPath("test-path").get();
        assertFalse(findStudy.isUseBanner());
    }

    @Test
    @DisplayName("스터디 tag form")
    @WithAccount("nick")
    void tag_form() throws Exception {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = studyFactory.createStudy(account);
        mockMvc.perform(get("/study/" + study.getPath() + "/settings/tags"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/tags"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("tags"));
    }

    @Test
    @DisplayName("스터디 tag 추가")
    @WithAccount("nick")
    void tag_add() throws Exception {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = studyFactory.createStudy(account);
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("Spring");
        mockMvc.perform(post("/study/" + study.getPath() + "/settings/tags/add")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
        )
                .andExpect(status().isOk());

        Study findStudy = studyRepository.findStudyWithTagsByPath(study.getPath()).get();
        assertTrue(findStudy.getTags().stream().filter(t -> t.getTitle().equals(tagForm.getTagTitle())).count() == 1);
    }

    @Test
    @DisplayName("스터디 tag 삭제")
    @WithAccount("nick")
    void tag_remove() throws Exception {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = studyFactory.createStudy(account);
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("Spring");
        studyService.addTag(study.getPath(), account, tagForm.getTagTitle());

        mockMvc.perform(post("/study/" + study.getPath() + "/settings/tags/remove")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                )
                .andExpect(status().isOk());

        Study findStudy = studyRepository.findStudyWithTagsByPath(study.getPath()).get();
        assertTrue(findStudy.getTags().isEmpty());
    }

    @Test
    @DisplayName("스터디 활동지역 form")
    @WithAccount("nick")
    void zone_form() throws Exception {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = studyFactory.createStudy(account);
        mockMvc.perform(get("/study/" + study.getPath() + "/settings/zones"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("zones"))
                .andExpect(view().name("study/settings/zones"));
    }

    @Test
    @DisplayName("스터디 활동지역 추가")
    @WithAccount("nick")
    void zone_add() throws Exception {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = studyFactory.createStudy(account);
        Zone zone = zoneRepository.findByCityAndProvince("Ansan", "Gyeonggi").get();
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(zone.toString());

        mockMvc.perform(post("/study/" + study.getPath() + "/settings/zones/add")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm))
        )
                .andExpect(status().isOk());

        Study findStudy = studyRepository.findStudyWithZonesByPath(study.getPath()).get();
        assertTrue(findStudy.getZones().contains(zone));
    }

    @Test
    @DisplayName("스터디 활동지역 삭제")
    @WithAccount("nick")
    void zone_remove() throws Exception {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = studyFactory.createStudy(account);
        Zone zone = zoneRepository.findByCityAndProvince("Ansan", "Gyeonggi").get();
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(zone.toString());

        studyService.addZone(study.getPath(), account, zone);

        mockMvc.perform(post("/study/" + study.getPath() + "/settings/zones/remove")
                .with(csrf())
                .content(objectMapper.writeValueAsString(zoneForm))
                .contentType(APPLICATION_JSON)
        )
                .andExpect(status().isOk());

        Study findStudy = studyRepository.findStudyWithZonesByPath(study.getPath()).get();
        assertTrue(findStudy.getZones().isEmpty());
    }

    @Test
    @DisplayName("스터디 폼")
    @WithAccount("nick")
    void study_form() throws Exception {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = studyFactory.createStudy(account);
        mockMvc.perform(get("/study/" + study.getPath() + "/settings/study"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("studyTitleForm"))
                .andExpect(model().attributeExists("studyPathForm"))
                .andExpect(view().name("study/settings/study"));
    }

    @Test
    @DisplayName("스터디 오픈")
    @WithAccount("nick")
    void study_open() throws Exception {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = studyFactory.createStudy(account);
        mockMvc.perform(post("/study/" + study.getPath() + "/settings/study/publish")
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl(String.format("/study/%s/settings/study", study.getPath())));


        Study findStudy = studyRepository.findById(study.getId()).get();
        assertTrue(findStudy.isPublished());
        assertFalse(findStudy.isClosed());
    }

    @Test
    @DisplayName("스터디 종료")
    @WithAccount("nick")
    void study_close() throws Exception {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = studyFactory.createStudy(account);
        studyService.publish(study.getPath(), account);

        mockMvc.perform(post("/study/" + study.getPath() + "/settings/study/close")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl(String.format("/study/%s/settings/study", study.getPath())));

        Study findStudy = studyRepository.findById(study.getId()).get();
        assertTrue(findStudy.isClosed());
    }

    @Test
    @DisplayName("스터디 경로 수정")
    @WithAccount("nick")
    void study_path() throws Exception {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = studyFactory.createStudy(account);
        mockMvc.perform(post("/study/" + study.getPath() + "/settings/study/path")
                        .with(csrf())
                        .param("newPath", "aaa")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl(String.format("/study/%s/settings/study", "aaa")));
    }

    @Test
    @DisplayName("스터디 이름 수정")
    @WithAccount("nick")
    void study_title() throws Exception {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = studyFactory.createStudy(account);
        mockMvc.perform(post("/study/" + study.getPath() + "/settings/study/title")
                        .with(csrf())
                        .param("newTitle", "new Title")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl(String.format("/study/%s/settings/study", study.getPath())));

        Study findStudy = studyRepository.findById(study.getId()).get();
        assertEquals("new Title", findStudy.getTitle());
    }

    @Test
    @DisplayName("스터디 팀원 모집 시작")
    @WithAccount("nick")
    void study_recruit_start() throws Exception {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = studyFactory.createStudy(account);
        studyService.publish(study.getPath(), account);

        mockMvc.perform(post("/study/" + study.getPath() + "/settings/recruit/start")
                .with(csrf())
        )
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/settings/study"))
                .andExpect(flash().attributeExists("message"));

        Study findStudy = studyRepository.findById(study.getId()).get();
        assertTrue(findStudy.isRecruiting());
    }

    @Test
    @DisplayName("스터디 팀원 모집 종료")
    @WithAccount("nick")
    void study_recruit_stop() throws Exception {
        Account account = accountRepository.findByNickname("nick").get();
        Study study = studyFactory.createStudy(account);
        studyService.publish(study.getPath(), account);

        mockMvc.perform(post("/study/" + study.getPath() + "/settings/recruit/stop")
                        .with(csrf())
                )
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/settings/study"))
                .andExpect(flash().attributeExists("message"));

        Study findStudy = studyRepository.findById(study.getId()).get();
        assertFalse(findStudy.isRecruiting());
    }
}