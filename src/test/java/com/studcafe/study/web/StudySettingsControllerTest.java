package com.studcafe.study.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studcafe.account.domain.Account;
import com.studcafe.account.repository.AccountRepository;
import com.studcafe.account.web.dto.TagForm;
import com.studcafe.security.annotation.WithAccount;
import com.studcafe.study.domain.Study;
import com.studcafe.study.repository.StudyRepository;
import com.studcafe.study.service.StudyService;
import com.studcafe.tag.domain.Tag;
import com.studcafe.tag.service.TagService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StudySettingsControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private StudyRepository studyRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private StudyService studyService;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TagService tagService;

    @AfterEach
    void afterEach() {
        studyRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("스터디 description 수정 form")
    @WithAccount("nick")
    void description_form() throws Exception {
        Study study = createStudy();

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
        Study study = createStudy();

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
        Study study = createStudy();
        mockMvc.perform(get("/study/" + study.getPath() + "/settings/banner"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }

    @Test
    @DisplayName("스터디 banner enable")
    @WithAccount("nick")
    void banner_enable() throws Exception {
        Study study = createStudy();
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
        Study study = createStudy();
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
        Study study = createStudy();
        mockMvc.perform(get("/study/" + study.getPath() + "/settings/tags"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("tags"));
    }

    @Test
    @DisplayName("스터디 tag 추가")
    @WithAccount("nick")
    void tag_add() throws Exception {
        Study study = createStudy();
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("Spring");
        mockMvc.perform(post("/study/" + study.getPath() + "/settings/tags/add")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
        )
                .andExpect(status().isOk());

        Study findStudy = studyRepository.findAccountWithTagsByPath(study.getPath()).get();
        assertTrue(findStudy.getTags().stream().filter(t -> t.getTitle().equals(tagForm.getTagTitle())).count() == 1);
    }

    @Test
    @DisplayName("스터디 tag 삭제")
    @WithAccount("nick")
    void tag_remove() throws Exception {
        Study study = createStudy();
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("Spring");
        Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
        studyService.addTag(study, tag);

        mockMvc.perform(post("/study/" + study.getPath() + "/settings/tags/remove")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                )
                .andExpect(status().isOk());

        Study findStudy = studyRepository.findAccountWithTagsByPath(study.getPath()).get();
        assertTrue(findStudy.getTags().isEmpty());
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