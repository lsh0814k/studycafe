package com.studcafe.account.web;

import com.studcafe.account.domain.Account;
import com.studcafe.account.service.AccountService;
import com.studcafe.account.web.dto.Profile;
import com.studcafe.main.annotation.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    static final String SETTINGS_PROFILE_VIEW_NAME = "/settings/profile";
    static final String SETTINGS_PROFILE_URL = "/settings/profile";

    private final AccountService accountService;

    @GetMapping(SETTINGS_PROFILE_URL)
    public String profileUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new Profile(account));

        return SETTINGS_PROFILE_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PROFILE_URL)
    public String updateProfile(@CurrentUser Account account, RedirectAttributes redirectAttributes,
                                @ModelAttribute @Valid Profile profile, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(profile);
            model.addAttribute(account);
            return SETTINGS_PROFILE_VIEW_NAME;
        }

        Account modifiedAccount = profile.createAccount();
        accountService.updateProfile(account.getEmail(), modifiedAccount);
        // TODO principal Account 동기화 시켜줘야 한다. 어떻게 처리를해야 좋을까,,,
        account.updateProfile(modifiedAccount);

        // 1회용 attribute
        redirectAttributes.addFlashAttribute("message", "프로필을 수정했습니다.");
        return "redirect:" + SETTINGS_PROFILE_URL;
    }
}
