package com.studcafe.account.web;

import com.studcafe.account.exception.UnMatchedTokenException;
import com.studcafe.account.service.AccountService;
import com.studcafe.account.web.validator.SignUpFormValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;


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

        accountService.processNewAccount(signUpForm.createAccount(passwordEncoder));
        return "redirect:/";
    }
}
