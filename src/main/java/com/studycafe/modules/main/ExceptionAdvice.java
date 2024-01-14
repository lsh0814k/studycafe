package com.studycafe.modules.main;

import com.studycafe.modules.account.annotation.CurrentUser;
import com.studycafe.modules.account.domain.Account;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(value = RuntimeException.class)
    public String handleRuntimeException(@CurrentUser Account account, HttpServletRequest req, RuntimeException e) {
        if (account != null) {
            log.info("{} requested {}", account.getNickname(), req.getRequestURI());
        } else {
            log.info("requested {}", req.getRequestURI());
        }
        log.error("bad request", e);

        return "error";
    }
}
