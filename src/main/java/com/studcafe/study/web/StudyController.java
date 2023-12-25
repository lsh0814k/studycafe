package com.studcafe.study.web;

import com.studcafe.account.domain.Account;
import com.studcafe.main.annotation.CurrentUser;
import com.studcafe.study.domain.Study;
import com.studcafe.study.repository.StudyRepository;
import com.studcafe.study.service.StudyService;
import com.studcafe.study.web.dto.StudyForm;
import com.studcafe.study.web.dto.StudyQueryForm;
import com.studcafe.study.web.validator.StudyFormValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;

import static java.nio.charset.StandardCharsets.UTF_8;

@Controller
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;
    private final StudyFormValidator studyFormValidator;
    private final StudyRepository studyRepository;

    @InitBinder("studyForm")
    public void studyFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(studyFormValidator);
    }

    @GetMapping("/new-study")
    public String newStudyForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new StudyForm());

        return "study/form";
    }

    @PostMapping("/new-study")
    public String newStudy(@CurrentUser Account account, @ModelAttribute("studyForm") @Valid StudyForm studyForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            return "study/form";
        }

        studyService.createNewStudy(account, studyForm.createStudy());
        return "redirect:/study/" + URLEncoder.encode(studyForm.getPath(), UTF_8);
    }

    @GetMapping("/study/{path}")
    public String viewStudy(@CurrentUser Account account, @PathVariable("path") String path, Model model) {
        Study study = studyRepository.findAllByPath(path).orElseThrow(() -> new IllegalStateException("존재하지 않는 스터디 입니다."));

        model.addAttribute("study", StudyQueryForm.createForm(study, account));
        model.addAttribute(account);

        return "study/view";
    }

    @GetMapping("/study/{path}/members")
    public String viewStudyMembers(@CurrentUser Account account, @PathVariable("path") String path, Model model) {
        Study study = studyRepository.findAllByPath(path).orElseThrow(() -> new IllegalStateException("존재하지 않는 스터디 입니다."));

        model.addAttribute("study", StudyQueryForm.createForm(study, account));
        model.addAttribute(account);

        return "study/members";
    }

    @PostMapping("/study/{path}/join")
    public String joinStudy(@CurrentUser Account account, @PathVariable("path") String path) {
        studyService.addMember(path, account);
        return String.format("redirect:/study/%s/members", path);
    }

    @PostMapping("/study/{path}/leave")
    public String leaveStudy(@CurrentUser Account account, @PathVariable("path") String path) {
        studyService.removeMember(path, account);
        return String.format("redirect:/study/%s/members", path);
    }
}
