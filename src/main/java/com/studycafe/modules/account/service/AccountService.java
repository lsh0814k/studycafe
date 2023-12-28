package com.studycafe.modules.account.service;

import com.studycafe.modules.account.domain.Account;
import com.studycafe.modules.tag.domain.Tag;
import com.studycafe.modules.account.exception.UnMatchedTokenException;
import com.studycafe.modules.account.repository.AccountRepository;
import com.studycafe.modules.tag.service.TagService;
import com.studycafe.modules.zone.domain.Zone;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final TagService tagService;

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

    public void verifyEmail(String email, String token) {
        Account account = accountRepository.findByEmail(email).orElseThrow(UnMatchedTokenException::new);
        if (!account.isValidToken(token)) {
            throw new UnMatchedTokenException();
        }

        account.completeSignUp();
    }

    public void sendConfirmEmail(Account account) {
        account.generateEmailCheckToken();
        sendSignUpConfirmEmail(account);
    }

    public void updateProfile(String email, Account updatedAccount) {
        Account findAccount = accountRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일 입니다."));
        findAccount.updateProfile(updatedAccount);
    }

    public void updatePassword(String email, Account account) {
        Account findAccount = accountRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일 입니다."));
        findAccount.updatePassword(account);
    }

    public void updateNotifications(String email, Account account) {
        Account findAccount = accountRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일 입니다."));
        findAccount.updateNotifications(account);
    }

    public void addTag(String email, String title) {
        Tag tag = tagService.findOrCreateNew(title);
        Account findAccount = accountRepository.findWithTagsByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일 입니다."));
        findAccount.addTags(tag);
    }

    @Transactional(readOnly = true)
    public Set<Tag> getTags(String email) {
        Account findAccount = accountRepository.findWithTagsByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일 입니다."));
        return findAccount.getTags();
    }

    public void removeTag(String email, Tag tag) {
        Account findAccount = accountRepository.findWithTagsByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일 입니다."));

        findAccount.getTags().remove(tag);
    }

    public void addZone(String email, Zone zone) {
        Account findAccount = accountRepository.findWithTagsByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일 입니다."));
        findAccount.getZones().add(zone);
    }

    @Transactional(readOnly = true)
    public Set<Zone> getZones(String email) {
        Account findAccount = accountRepository.findWithZonesByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일 입니다."));
        return findAccount.getZones();
    }

    public void removeZone(String email, Zone zone) {
        Account findAccount = accountRepository.findWithZonesByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일 입니다."));
        findAccount.getZones().remove(zone);
    }
}
