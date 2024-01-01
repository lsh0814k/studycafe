package com.studycafe.infra.mail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder @Getter
@AllArgsConstructor
public class EmailMessage {
    private String to;
    private String subject;
    private String message;
}
