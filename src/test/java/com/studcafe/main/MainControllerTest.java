package com.studcafe.main;

import com.studcafe.account.repository.AccountRepository;
import com.studcafe.account.service.AccountService;
import com.studcafe.account.web.dto.SignUpForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {
    @Autowired private AccountService accountService;
    @Autowired private MockMvc mockMvc;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AccountRepository accountRepository;

    @Test
    @DisplayName("이메일로 로그인 성공")
    void login_with_email() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "nick@email.com")
                        .param("password", "123456789")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("nick"));

    }

    @Test
    @DisplayName("닉네임으로 로그인 성공")
    void login_with_nickname() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "nick")
                        .param("password", "123456789")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("nick"));

    }


    @Test
    @DisplayName("로그인 실패")
    void login_fail() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "n1123")
                        .param("password", "1234556789")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());

    }

    @Test
    @WithMockUser
    @DisplayName("로그아웃")
    void logout() throws Exception {
        mockMvc.perform(post("/logout")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }

    @BeforeEach
    void beforeEach() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail("nick@email.com");
        signUpForm.setNickname("nick");
        signUpForm.setPassword("123456789");
        accountService.processNewAccount(signUpForm.createAccount(passwordEncoder));
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }
}