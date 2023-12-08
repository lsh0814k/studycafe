package com.studcafe.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

import static jakarta.persistence.FetchType.*;
import static lombok.AccessLevel.*;

@Entity @Getter @EqualsAndHashCode(of ="id") @Setter
@Builder @AllArgsConstructor @NoArgsConstructor(access = PROTECTED)
public class Account {

    @Id @GeneratedValue
    private Long id;

    // ---- 로그인 ----
    @Column(unique = true) // 로그인 시 유니크 해야 하는 필드
    private String email;

    @Column(unique = true) // 로그인 시 유니크 해야 하는 필드
    private String nickname;

    private String password;

    private boolean emailVerified; // 이메일 인증 절차(이메일 인증 여부를 검토)

    private String emailCheckToken; // 이메일 검증 시 필요한 토큰

    private LocalDateTime joinedAt; // 검증 후 가입 날짜 설정

    // ---- 프로필 ----
    private String bio; // 짧은 자기소개

    private String url; // 웹 사이트 url

    private String occupation; // 직업

    private String location; // 지역

    @Lob @Basic(fetch = EAGER)
    private String profileImage; // 프로필 이미지

    // ---- 알림 설정 ----
    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb;

    private boolean studyUpdatedByEmail;

    private boolean studyUpdatedByWeb;

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
    }
}