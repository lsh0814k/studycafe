package com.studcafe.account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

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
}
