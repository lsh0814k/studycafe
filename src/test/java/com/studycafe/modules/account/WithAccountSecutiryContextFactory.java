package com.studycafe.modules.account;

import com.studycafe.modules.account.annotation.WithAccount;
import com.studycafe.modules.account.service.AccountService;
import com.studycafe.modules.account.service.CustomUserDetailsService;
import com.studycafe.modules.account.web.dto.SignUpForm;
import com.studycafe.modules.account.web.dto.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@RequiredArgsConstructor
public class WithAccountSecutiryContextFactory implements WithSecurityContextFactory<WithAccount> {

    private final AccountService accountService;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SecurityContext createSecurityContext(WithAccount annotation) {

        createMember();

        String nickname = annotation.value();
        UserAccount principal = customUserDetailsService.loadUserByUsername(nickname);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }

    private void createMember() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail("nick@email.com");
        signUpForm.setNickname("nick");
        signUpForm.setPassword("123456789");
        accountService.processNewAccount(signUpForm.createAccount(passwordEncoder));
    }

}
