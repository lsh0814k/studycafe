package com.studycafe.modules.account.web.dto;

import com.studycafe.modules.account.domain.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter @Setter
@NoArgsConstructor
public class PasswordForm {
    @Length(min= 8, max = 50)
    private String newPassword;
    @Length(min= 8, max = 50)
    private String newPasswordConfirm;

    public Account createAccount(PasswordEncoder passwordEncoder) {
        return Account.builder()
                .password(passwordEncoder.encode(newPassword))
                .build();
    }
}
