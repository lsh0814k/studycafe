package com.studcafe.account.web.dto;

import com.studcafe.account.domain.Account;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter @Setter
public class SignUpForm {
    @NotBlank
    @Length(min = 3, max = 20)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{3,20}$")
    private String nickname;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Length(min = 8, max = 50)
    private String password;


    public Account createAccount(PasswordEncoder passwordEncoder) {
        return Account.builder()
                .email(email)
                .nickname(nickname)
                .password(passwordEncoder.encode(password)) // TODO encoding
                .build();
    }
}
