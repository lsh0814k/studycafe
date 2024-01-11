package com.studycafe.modules.main;

import com.studycafe.modules.account.annotation.CurrentUser;
import com.studycafe.modules.account.domain.Account;
import com.studycafe.modules.study.domain.Study;
import com.studycafe.modules.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final StudyRepository studyRepository;
    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model,
                       @PageableDefault(size = 9, sort = "publishedDateTime", direction = DESC) Pageable pageable) {
        if (account != null) {
            model.addAttribute(account);
        }

        List<Study> list = studyRepository.findByMainPage(pageable);
        model.addAttribute("studyList", list);

        return "index";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/search/study")
    public String searchStudy(@ModelAttribute("keyword") String keyword, Model model,
                              @PageableDefault(size = 9, sort = "publishedDateTime", direction = DESC) Pageable pageable) {
        Page<Study> studyPage = studyRepository.findByKeyword(keyword, pageable);
        model.addAttribute("studyPage", studyPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty", "publishedDateTime");
        return "search";
    }
}
