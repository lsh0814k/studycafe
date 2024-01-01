package com.studycafe.infra.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("local")
@Component
@Slf4j
public class ConsoleMailService implements EmailService {
    @Override
    public void sendEmail(EmailMessage emailMessage) {
        log.info("send email: {} ", emailMessage.getMessage());
    }
}
