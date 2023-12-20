package com.studcafe.account.service;

import com.studcafe.account.domain.Account;
import com.studcafe.tag.domain.Tag;
import com.studcafe.account.exception.UnMatchedTokenException;
import com.studcafe.account.repository.AccountRepository;
import com.studcafe.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final TagRepository tagRepository;

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

    @Transactional
    public void sendConfirmEmail(Account account) {
        account.generateEmailCheckToken();
        sendSignUpConfirmEmail(account);
    }

    @Transactional
    public void updateProfile(String email, Account updatedAccount) {
        Account findAccount = accountRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일 입니다."));
        findAccount.updateProfile(updatedAccount);
    }

    @Transactional
    public void updatePassword(String email, Account account) {
        Account findAccount = accountRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일 입니다."));
        findAccount.updatePassword(account);
    }

    @Transactional
    public void updateNotifications(String email, Account account) {
        Account findAccount = accountRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일 입니다."));
        findAccount.updateNotifications(account);
    }

    @Transactional
    public void addTag(String email, String title) {
        Account findAccount = accountRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일 입니다."));
        Tag tag = tagRepository.findByTitle(title).orElseGet(() -> tagRepository.save(Tag.builder().title(title).build()));

        findAccount.addTags(tag);
    }

    public Set<Tag> getTags(String email) {
        Account findAccount = accountRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일 입니다."));
        return findAccount.getTags();
    }

    @Transactional
    public void removeTag(String email, String tagTitle) {
        Account findAccount = accountRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일 입니다."));
        Tag tag = tagRepository.findByTitle(tagTitle).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일 입니다."));
        findAccount.getTags().remove(tag);
    }
}
