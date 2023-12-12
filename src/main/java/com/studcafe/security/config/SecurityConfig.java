package com.studcafe.security.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        // 특정 요청들을 authorize 체크를 하지 않도록 설정할 수 있다.
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);
        http.authorizeHttpRequests((requests) -> requests
                .requestMatchers(
                        mvcMatcherBuilder.pattern("/"),
                        mvcMatcherBuilder.pattern("/login"),
                        mvcMatcherBuilder.pattern("/sign-up"),
                        mvcMatcherBuilder.pattern("/check-email"),
                        mvcMatcherBuilder.pattern("/check-email-token"),
                        mvcMatcherBuilder.pattern("/email-login"),
                        mvcMatcherBuilder.pattern("/check-email-login"),
                        mvcMatcherBuilder.pattern("/login-link"),
                        mvcMatcherBuilder.pattern(HttpMethod.GET, "/profile/*")
                ).permitAll() // 해당 요청들은 모두 허용한다.
                .anyRequest().authenticated() // 그 외 요청들은 모두 인증해야 한다.
        );

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/node_modules/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
