package com.studcafe.account.web;

import com.studcafe.account.domain.Account;
import com.studcafe.account.service.AccountService;
import com.studcafe.account.web.dto.Notifications;
import com.studcafe.account.web.dto.PasswordForm;
import com.studcafe.account.web.dto.Profile;
import com.studcafe.account.web.validator.PasswordValidator;
import com.studcafe.main.annotation.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    static final String SETTINGS_PROFILE_VIEW_NAME = "/settings/profile";
    static final String SETTINGS_PROFILE_URL = "/settings/profile";

    static final String SETTINGS_PASSWORD_VIEW_NAME = "/settings/password";
    static final String SETTINGS_PASSWORD_URL = "/settings/password";

    static final String SETTINGS_NOTIFICATIONS_VIEW_NAME = "/settings/notifications";
    static final String SETTINGS_NOTIFICATIONS_URL = "/settings/notifications";

    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new PasswordValidator());
    }

    @GetMapping(SETTINGS_NOTIFICATIONS_URL)
    public String updateNotificationsForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new Notifications(account));

        return SETTINGS_NOTIFICATIONS_VIEW_NAME;
    }

    @PostMapping(SETTINGS_NOTIFICATIONS_URL)
    public String updateNotifications(@CurrentUser Account account, RedirectAttributes redirectAttributes,
                                      @ModelAttribute @Valid Notifications notifications, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(notifications);
            return SETTINGS_NOTIFICATIONS_VIEW_NAME;
        }

        Account modifiedAccount = notifications.createAccount();
        accountService.updateNotifications(account.getEmail(), modifiedAccount);
        // TODO principal Account 동기화 시켜줘야 한다. 어떻게 처리를해야 좋을까,,,
        account.updateNotifications(modifiedAccount);
        redirectAttributes.addFlashAttribute("message", "알림을 수정했습니다.");
        return "redirect:" + SETTINGS_NOTIFICATIONS_VIEW_NAME;
    }

    @GetMapping(SETTINGS_PASSWORD_URL)
    public String passwordForm(@CurrentUser Account account, Model model) {
        model.addAttribute(new PasswordForm());
        model.addAttribute(account);
        return SETTINGS_PASSWORD_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PASSWORD_URL)
    public String password(@CurrentUser Account account, RedirectAttributes redirectAttributes,
                           @ModelAttribute @Valid PasswordForm passwordForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(passwordForm);
            return SETTINGS_PASSWORD_VIEW_NAME;
        }

        Account modifiedAccount = passwordForm.createAccount(passwordEncoder);
        accountService.updatePassword(account.getEmail(), modifiedAccount);
        // TODO principal Account 동기화 시켜줘야 한다. 어떻게 처리를해야 좋을까,,,
        account.updatePassword(modifiedAccount);
        redirectAttributes.addFlashAttribute("message", "패스워드를 수정했습니다.");
        return "redirect:" + SETTINGS_PASSWORD_URL;
    }


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
