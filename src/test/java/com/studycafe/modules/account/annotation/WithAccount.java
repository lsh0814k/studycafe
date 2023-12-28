package com.studycafe.modules.account.annotation;

import com.studycafe.modules.account.WithAccountSecutiryContextFactory;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.*;

@Retention(RUNTIME)
@WithSecurityContext(factory = WithAccountSecutiryContextFactory.class)
public @interface WithAccount {
    String value();
}
