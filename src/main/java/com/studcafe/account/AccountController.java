package com.studcafe.account;

import com.studcafe.domain.Account;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;

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

        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getEmail())
                .password(signUpForm.getPassword()) // TODO encoding
                .build();
        account.generateEmailCheckToken();
        accountRepository.save(account);

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject("스토디카페, 회원 가입 인증");
        simpleMailMessage.setText(String.format("/check-email-token?token=%s&email=%s", account.getEmailCheckToken(), account.getEmail()));
        simpleMailMessage.setTo(account.getEmail());
        javaMailSender.send(simpleMailMessage);

        return "redirect:/";
    }

}
