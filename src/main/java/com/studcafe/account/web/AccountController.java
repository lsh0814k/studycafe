package com.studcafe.account.web;

import com.studcafe.account.domain.Account;
import com.studcafe.account.exception.UnMatchedTokenException;
import com.studcafe.account.repository.AccountRepository;
import com.studcafe.account.service.AccountService;
import com.studcafe.account.web.dto.SignUpForm;
import com.studcafe.account.web.validator.SignUpFormValidator;
import com.studcafe.main.annotation.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;


    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String joinForm(Model model) {
        // 타임리프가 의존성 주입이 되어 있다면
        // org.springframework.boot.autoconfigure.thymeleaf가 자동 설정 된다.
        // spring:
        //  thymeleaf:
        //    prefix: classpath:/templates/
        //    suffix: .html
        model.addAttribute(new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid SignUpForm signUpForm, BindingResult bindingResult) {
        if( bindingResult.hasErrors()) {
            return "account/sign-up";
        }

        Account account = signUpForm.createAccount(passwordEncoder);
        accountService.processNewAccount(account);
        return "redirect:/login";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(@RequestParam("token") String token, @RequestParam("email") String email, Model model) {
        try {
            accountService.verifyEmail(email, token);
            Account account = accountRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일 입니다."));
            model.addAttribute("numberOfUser", accountRepository.count());
            model.addAttribute("nickname", account.getNickname());
        } catch (UnMatchedTokenException e) {
            model.addAttribute("error", "wrong approach");
        }

        return "account/checked-email";
    }

    @GetMapping("/check-email")
    public String checkEmail(@CurrentUser Account account, Model model) {
        model.addAttribute("email", account.getEmail());

        return "account/check-email";
    }

    @GetMapping("/resend-confirm-email")
    public String resendConfirmEmail(@CurrentUser Account account, Model model) {
        if (!account.canSendConfirmEmail()) {
            model.addAttribute("error", "인증 이메일은 1시간에 한번만 전송할 수 있습니다.");
            model.addAttribute("email", account.getEmail());
            return "account/check-email";
        }

        accountService.sendConfirmEmail(account);
        return "redirect:/";
    }

    @GetMapping("/profile/{nickname}")
    public String viewProfile(@PathVariable("nickname") String nickname, Model model, @CurrentUser Account account) {
        Account byNickname = accountRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException(nickname + "에 해당하는 사용자가 없습니다."));

        model.addAttribute(byNickname);
        model.addAttribute("isOwner", byNickname.equals(account));

        return "account/profile";
    }
}
