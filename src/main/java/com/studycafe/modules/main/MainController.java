package com.studycafe.modules.main;

import com.studycafe.modules.account.domain.Account;
import com.studycafe.modules.account.annotation.CurrentUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model) {
        if (account != null) {
            model.addAttribute(account);
        }

        return "index";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }
}
