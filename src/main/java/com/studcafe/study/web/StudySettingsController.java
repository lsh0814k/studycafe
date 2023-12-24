package com.studcafe.study.web;

import com.studcafe.account.domain.Account;
import com.studcafe.main.annotation.CurrentUser;
import com.studcafe.study.domain.Study;
import com.studcafe.study.repository.StudyRepository;
import com.studcafe.study.service.StudyService;
import com.studcafe.study.web.dto.StudyDescriptionForm;
import com.studcafe.study.web.dto.StudyQueryForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;

import static java.nio.charset.StandardCharsets.UTF_8;

@Controller
@RequestMapping("/study/{path}/settings")
@RequiredArgsConstructor
public class StudySettingsController {
    private final StudyRepository studyRepository;
    private final StudyService studyService;

    @GetMapping("/description")
    public String descriptionForm(@CurrentUser Account account, @PathVariable("path") String path, Model model) {
        Study study = studyRepository.findAllByPath(path).orElseThrow(() -> new IllegalStateException("존재하지 않는 스터디 입니다."));
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
}
