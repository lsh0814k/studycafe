package com.studycafe.modules.account;

import com.studycafe.modules.account.domain.Account;
import com.studycafe.modules.account.service.AccountService;
import com.studycafe.modules.account.web.dto.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountFactory {
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;

    public Account createAccount(String nickname) {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail(nickname + "@email.com");
        signUpForm.setNickname(nickname);
        signUpForm.setPassword("123456789");
        Account account = signUpForm.createAccount(passwordEncoder);
        accountService.processNewAccount(account);

        return account;
    }
}
