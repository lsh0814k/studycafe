package com.studcafe.account.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studcafe.account.domain.Account;
import com.studcafe.account.web.dto.*;
import com.studcafe.tag.domain.Tag;
import com.studcafe.account.service.AccountService;
import com.studcafe.account.web.validator.PasswordValidator;
import com.studcafe.main.annotation.CurrentUser;
import com.studcafe.tag.repository.TagRepository;
import com.studcafe.zone.domain.Zone;
import com.studcafe.zone.repository.ZoneRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    static final String SETTINGS_PROFILE_VIEW_NAME = "/settings/profile";
    static final String SETTINGS_PROFILE_URL = "/settings/profile";

    static final String SETTINGS_PASSWORD_VIEW_NAME = "/settings/password";
    static final String SETTINGS_PASSWORD_URL = "/settings/password";

    static final String SETTINGS_NOTIFICATIONS_VIEW_NAME = "/settings/notifications";
    static final String SETTINGS_NOTIFICATIONS_URL = "/settings/notifications";

    static final String SETTINGS_TAGS_VIEW_NAME = "/settings/tags";
    static final String SETTINGS_TAGS_URL = "/settings/tags";

    static final String SETTINGS_ZONES_VIEW_NAME = "/settings/zones";
    static final String SETTINGS_ZONES_URL = "/settings/zones";

    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final TagRepository tagRepository;
    private final ZoneRepository zoneRepository;

    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new PasswordValidator());
    }


    @GetMapping(SETTINGS_ZONES_URL)
    public String zonesForm(@CurrentUser Account account, Model model) throws JsonProcessingException {
        Set<Zone> zones = accountService.getZones(account.getEmail());
        model.addAttribute("zones", zones.stream().map(Zone::toString).toList());

        List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).toList();
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));

        model.addAttribute(account);

        return SETTINGS_ZONES_VIEW_NAME;
    }

    @PostMapping(SETTINGS_ZONES_URL + "/add")
    @ResponseBody
    public ResponseEntity addZone(@CurrentUser Account account, @RequestBody ZoneForm zoneForm) {
        Optional<Zone> zoneOptional = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zoneOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        accountService.addZone(account.getEmail(), zoneOptional.get());
        return ResponseEntity.ok().build();
    }

    @PostMapping(SETTINGS_ZONES_URL + "/remove")
    @ResponseBody
    public ResponseEntity removeZone(@CurrentUser Account account, @RequestBody ZoneForm zoneForm) {
        Optional<Zone> zoneOptional = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zoneOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        accountService.removeZone(account.getEmail(), zoneOptional.get());
        return ResponseEntity.ok().build();
    }

    @GetMapping(SETTINGS_TAGS_URL)
    public String updateTagsForm(@CurrentUser Account account, Model model) throws JsonProcessingException {
        Set<Tag> tags = accountService.getTags(account.getEmail());
        model.addAttribute(account);
        model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toList()));
        model.addAttribute("whitelist", objectMapper.writeValueAsString(tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList())));

        return SETTINGS_TAGS_VIEW_NAME;
    }

    @PostMapping(SETTINGS_TAGS_URL + "/add")
    @ResponseBody
    public ResponseEntity addTag(@CurrentUser Account account, @RequestBody TagForm tagForm) {
        accountService.addTag(account.getEmail(), tagForm.getTagTitle());
        return ResponseEntity.ok().build();
    }

    @PostMapping(SETTINGS_TAGS_URL + "/remove")
    @ResponseBody
    public ResponseEntity removeTag(@CurrentUser Account account, @RequestBody TagForm tagForm) {
        Optional<Tag> tagOptional = tagRepository.findByTitle(tagForm.getTagTitle());
        if (tagOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        accountService.removeTag(account.getEmail(), tagOptional.get());
        return ResponseEntity.ok().build();
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
