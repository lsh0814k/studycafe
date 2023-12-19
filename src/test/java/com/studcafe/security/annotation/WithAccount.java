package com.studcafe.security.annotation;

import com.studcafe.security.WithAccountSecutiryContextFactory;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.*;

@Retention(RUNTIME)
@WithSecurityContext(factory = WithAccountSecutiryContextFactory.class)
public @interface WithAccount {
    String value();
}
