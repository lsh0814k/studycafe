package com.studycafe.modules.main;

import com.studycafe.modules.account.annotation.CurrentUser;
import com.studycafe.modules.account.domain.Account;
import com.studycafe.modules.account.repository.AccountRepository;
import com.studycafe.modules.event.repository.EventRepository;
import com.studycafe.modules.study.domain.Study;
import com.studycafe.modules.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    private final AccountRepository accountRepository;
    private final EventRepository eventRepository;
    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model) {
        if (account != null) {
            addModelByAfterLoginIndex(model, account);
            return "index-after-login";
        }

        model.addAttribute("studyList", studyRepository.findByMainPage());
        return "index";
    }

    private void addModelByAfterLoginIndex(Model model, Account account) {
        Account findAccount = accountRepository.findWithTagsAndZonesById(account.getId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        model.addAttribute("account", findAccount);
        model.addAttribute("studyManagerOf", studyRepository.findFirst5WithManagerByAccountId(account.getId()));
        model.addAttribute("studyMemberOf", studyRepository.findFirst5WithMemberByAccountId(account.getId()));
        model.addAttribute("enrollmentList", eventRepository.findEnrollmentWithEventAndStudyByAccountId(account.getId()));
        model.addAttribute("studyList", studyRepository.findByAccount(findAccount.getZones(), findAccount.getTags()));
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
