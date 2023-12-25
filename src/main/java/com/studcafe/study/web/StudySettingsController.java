package com.studcafe.study.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studcafe.account.domain.Account;
import com.studcafe.account.web.dto.TagForm;
import com.studcafe.account.web.dto.ZoneForm;
import com.studcafe.main.annotation.CurrentUser;
import com.studcafe.study.domain.Study;
import com.studcafe.study.repository.StudyRepository;
import com.studcafe.study.service.StudyService;
import com.studcafe.study.web.dto.StudyDescriptionForm;
import com.studcafe.study.web.dto.StudyPathForm;
import com.studcafe.study.web.dto.StudyQueryForm;
import com.studcafe.study.web.dto.StudyTitleForm;
import com.studcafe.study.web.validator.StudyPathFormValidator;
import com.studcafe.tag.domain.Tag;
import com.studcafe.tag.dto.TagsQueryForm;
import com.studcafe.tag.repository.TagRepository;
import com.studcafe.tag.service.TagService;
import com.studcafe.zone.domain.Zone;
import com.studcafe.zone.repository.ZoneRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

@Controller
@RequestMapping("/study/{path}/settings")
@RequiredArgsConstructor
public class StudySettingsController {
    private final StudyRepository studyRepository;
    private final StudyService studyService;
    private final TagRepository tagRepository;
    private final TagService tagService;
    private final ZoneRepository zoneRepository;
    private final ObjectMapper objectMapper;
    private final StudyPathFormValidator studyPathFormValidator;

    @InitBinder("studyPathForm")
    public void studyPathFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(studyPathFormValidator);
    }

    @GetMapping("/description")
    public String descriptionForm(@CurrentUser Account account, @PathVariable("path") String path, Model model) {
        Study study = studyService.getStudyToView(path, account);
        model.addAttribute(account);
        model.addAttribute("study", StudyQueryForm.createForm(study, account));
        model.addAttribute(new StudyDescriptionForm(study));
        return "study/settings/description";
    }

    @PostMapping("/description")
    public String updateDescription(@CurrentUser Account account, @PathVariable("path") String path, Model model, RedirectAttributes redirectAttributes,
                                    @ModelAttribute("studyDescriptionForm") @Valid StudyDescriptionForm studyDescriptionForm, BindingResult bindingResult) {
        Study study = studyService.getStudyToUpdate(path, account);
        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute("study", StudyQueryForm.createForm(study, account));
            return "study/settings/description";
        }

        studyService.updateStudyDescription(study.getId(), studyDescriptionForm.createStudy());
        redirectAttributes.addFlashAttribute("message", "스터디 소개를 수정했습니다.");
        return String.format("redirect:/study/%s/settings/description", URLEncoder.encode(path, UTF_8));
    }

    @GetMapping("/banner")
    public String studyBannerForm(@CurrentUser Account account, @PathVariable("path") String path, Model model) {
        Study study = studyService.getStudyToView(path, account);
        model.addAttribute(account);
        model.addAttribute("study", StudyQueryForm.createForm(study, account));

        return "study/settings/banner";
    }

    @PostMapping("/banner")
    public String updateStudyBanner(@CurrentUser Account account, @PathVariable("path") String path, RedirectAttributes redirectAttributes,
                                    @ModelAttribute("image") String image) {
        Study study = studyService.getStudyToUpdate(path, account);
        studyService.updateBanner(study.getId(), image);

        redirectAttributes.addFlashAttribute("message" ,"스터디 이미지를 수정했습니다.");
        return String.format("redirect:/study/%s/settings/banner", URLEncoder.encode(path, UTF_8));
    }

    @PostMapping("/banner/enable")
    public String enableBanner(@CurrentUser Account account, @PathVariable("path") String path, RedirectAttributes redirectAttributes) {
        Study study = studyService.getStudyToUpdate(path, account);
        studyService.updateUseBanner(study.getId(), true);

        return String.format("redirect:/study/%s/settings/banner", URLEncoder.encode(path, UTF_8));
    }

    @PostMapping("/banner/disable")
    public String disableBanner(@CurrentUser Account account, @PathVariable("path") String path, RedirectAttributes redirectAttributes) {
        Study study = studyService.getStudyToUpdate(path, account);
        studyService.updateUseBanner(study.getId(), false);

        return String.format("redirect:/study/%s/settings/banner", URLEncoder.encode(path, UTF_8));
    }

    @GetMapping("/tags")
    public String tagsForm(@CurrentUser Account account, @PathVariable("path") String path, Model model) throws JsonProcessingException {

        Study study = studyService.getStudyToView(path, account);
        StudyQueryForm studyQueryForm = StudyQueryForm.createForm(study, account);
        model.addAttribute(account);
        model.addAttribute("study", studyQueryForm);
        model.addAttribute("whitelist", objectMapper.writeValueAsString(tagRepository.findAll().stream().map(Tag::getTitle).toList()));
        model.addAttribute("tags", studyQueryForm.getTags().stream().map(TagsQueryForm::getTitle).toList());

        return "study/settings/tags";
    }

    @PostMapping("/tags/add")
    @ResponseBody
    public ResponseEntity addTag(@CurrentUser Account account, @PathVariable("path") String path, @RequestBody TagForm tagForm) {
        Study study = studyService.getStudyToUpdateTag(account, path);
        Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
        studyService.addTag(study, tag);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/tags/remove")
    @ResponseBody
    public ResponseEntity removeTags(@CurrentUser Account account, @PathVariable("path") String path, @RequestBody TagForm tagForm) {
        Optional<Tag> tagOptional = tagRepository.findByTitle(tagForm.getTagTitle());
        if (tagOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Study study = studyService.getStudyToUpdateTag(account, path);
        studyService.removeTag(study, tagOptional.get());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/zones")
    public String zonesForm(@CurrentUser Account account, @PathVariable("path") String path, Model model) throws JsonProcessingException {
        Study study = studyService.getStudyToUpdateZone(account, path);
        model.addAttribute(account);
        StudyQueryForm studyQueryForm = StudyQueryForm.createForm(study, account);
        model.addAttribute("study", studyQueryForm);
        model.addAttribute("whitelist", objectMapper.writeValueAsString(zoneRepository.findAll().stream().map(Zone::toString).toList()));
        model.addAttribute("zones", study.getZones().stream().map(Zone::toString).toList());
        return "study/settings/zones";
    }

    @PostMapping("/zones/add")
    @ResponseBody
    public ResponseEntity addZone(@CurrentUser Account account, @PathVariable("path") String path, @RequestBody ZoneForm zoneForm) {
        Optional<Zone> zoneOptional = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zoneOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Study study = studyService.getStudyToUpdateZone(account, path);
        studyService.addZone(study, zoneOptional.get());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/zones/remove")
    @ResponseBody
    public ResponseEntity removeZone(@CurrentUser Account account, @PathVariable("path") String path, @RequestBody ZoneForm zoneForm) {
        Optional<Zone> zoneOptional = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zoneOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Study study = studyService.getStudyToUpdateZone(account, path);
        studyService.removeZone(study, zoneOptional.get());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/study")
    public String studyForm(@CurrentUser Account account, @PathVariable("path") String path, Model model) {
        Study study = studyService.getStudyToView(path, account);
        model.addAttribute(account);
        model.addAttribute("study", StudyQueryForm.createForm(study, account));
        model.addAttribute(StudyTitleForm.builder().newTitle(study.getTitle()).build());
        model.addAttribute(StudyPathForm.builder().newPath(study.getPath()).build());
        return "study/settings/study";
    }

    @PostMapping("/study/publish")
    public String publishStudy(@CurrentUser Account account, @PathVariable("path") String path, RedirectAttributes redirectAttributes) {
        Study study = studyService.getStudyToUpdate(path, account);
        studyService.publish(study);

        redirectAttributes.addFlashAttribute("message", "스터디를 공개했습니다.");
        return String.format("redirect:/study/%s/settings/study", URLEncoder.encode(path, UTF_8));
    }

    @PostMapping("/study/close")
    public String closeStudy(@CurrentUser Account account, @PathVariable("path") String path, RedirectAttributes redirectAttributes) {
        Study study = studyService.getStudyToUpdate(path, account);
        studyService.close(study);

        redirectAttributes.addFlashAttribute("message", "스터디를 공개했습니다.");
        return String.format("redirect:/study/%s/settings/study", URLEncoder.encode(path, UTF_8));
    }

    @PostMapping("/study/path")
    public String updateStudyPath(@CurrentUser Account account, @PathVariable("path") String path, RedirectAttributes redirectAttributes, Model model,
                                  @ModelAttribute("studyPathForm") StudyPathForm studyPathForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Study study = studyService.getStudyToView(path, account);
            model.addAttribute(account);
            model.addAttribute("study", StudyQueryForm.createForm(study, account));

            return "study/settings/study";
        }

        Study study = studyService.getStudyToUpdate(path, account);
        studyService.updateStudyPath(study, studyPathForm.getNewPath());

        redirectAttributes.addFlashAttribute("message", "스터디 경로를 수정했습니다.");
        return String.format("redirect:/study/%s/settings/study", URLEncoder.encode(studyPathForm.getNewPath(), UTF_8));
    }

    @PostMapping("/study/title")
    public String updateStudyTitle(@CurrentUser Account account, @PathVariable("path") String path, RedirectAttributes redirectAttributes, Model model,
                                   @ModelAttribute("studyTitleForm") StudyTitleForm studyTitleForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Study study = studyService.getStudyToView(path, account);
            model.addAttribute(account);
            model.addAttribute("study", StudyQueryForm.createForm(study, account));

            return "study/settings/study";
        }
        Study study = studyService.getStudyToUpdate(path, account);
        studyService.updateStudyTitle(study, studyTitleForm.getNewTitle());

        redirectAttributes.addFlashAttribute("message", "스터디 이름을 수정했습니다.");
        return String.format("redirect:/study/%s/settings/study", URLEncoder.encode(path, UTF_8));
    }

    @PostMapping("/study/remove")
    public String removeStudy(@CurrentUser Account account, @PathVariable("path") String path) {
        Study study = studyService.getStudyToUpdate(path, account);
        studyService.remove(study);
        return "redirect:/";
    }

}
