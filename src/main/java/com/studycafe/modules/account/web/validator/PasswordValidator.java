package com.studycafe.modules.account.web.validator;

import com.studycafe.modules.account.web.dto.PasswordForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PasswordValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(PasswordForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordForm passwordForm = (PasswordForm) target;
        if (!passwordForm.getNewPassword().equals(passwordForm.getNewPasswordConfirm())) {
            errors.rejectValue("newPassword", "wrong.value", "입력한 새 패스워드가 일치하지 않습니다.");
        }
    }
}
