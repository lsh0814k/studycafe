package com.studcafe.account.service;

import com.studcafe.account.domain.Account;
import com.studcafe.security.UserAccount;
import com.studcafe.account.exception.UnMatchedTokenException;
import com.studcafe.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;

    @Transactional
    public void processNewAccount(Account account) {
        account.generateEmailCheckToken();
        saveNewAccount(account);
        sendSignUpConfirmEmail(account);
    }

    private void saveNewAccount(Account account) {
        accountRepository.save(account);
    }

    private void sendSignUpConfirmEmail(Account account) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject("스토디카페, 회원 가입 인증");
        simpleMailMessage.setText(String.format("/check-email-token?token=%s&email=%s", account.getEmailCheckToken(), account.getEmail()));
        simpleMailMessage.setTo(account.getEmail());
        javaMailSender.send(simpleMailMessage);
    }

    @Transactional
    public void verifyEmail(String email, String token) {
        Account account = accountRepository.findByEmail(email).orElseThrow(UnMatchedTokenException::new);
        if (!account.isValidToken(token)) {
            throw new UnMatchedTokenException();
        }

        account.completeSignUp();
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);
    }
}
