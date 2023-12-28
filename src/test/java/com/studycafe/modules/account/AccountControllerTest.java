package com.studycafe.modules.account;

import com.studycafe.modules.account.domain.Account;
import com.studycafe.modules.account.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    JavaMailSender javaMailSender;

    @BeforeEach
    public void init() {
        accountRepository.deleteAll();
    }

    /**
     * security가 적용 되어 있으면 sign-up 요청이 왔을 때
     * AccessDeniedException이 발생한다.
     * 이때 security Filter가 해당 exception을 catch해
     * form generation과 함께 /login으로 rediredt 시킨다.
     */
    @Test
    @DisplayName("회원 가입 화면 보이는지 테스트")
    void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("회원 가입 처리 - csrf 값이 없어 403 에러 발생")
    void signUpSubmit_no_csrf() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "nick")
                .param("email", "email..")
                .param("password", "12345")
        )
        .andExpect(status().isForbidden())
        .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("회원 가입 처리 - 입력 값 오류")
    void signUpSubmit_with_wrong_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .with(csrf())
                        .param("nickname", "nick")
                        .param("email", "email..")
                        .param("password", "12345")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("회원 가입 처리 - 입력 값 정상")
    void signUp_with_correct_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .with(csrf())
                        .param("nickname", "nick")
                        .param("email", "email@naver.com")
                        .param("password", "a151385wa3!")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"))
                .andExpect(unauthenticated());

        assertTrue(accountRepository.existsByEmail("email@naver.com"));
        assertTrue(accountRepository.existsByNickname("nick"));
    }

    @Test
    @DisplayName("회원 가입 처리 - 입력 값 정상(메일 발송 여부)")
    void signUp_with_correct_input_send_mail() throws Exception{
        mockMvc.perform(post("/sign-up")
                        .with(csrf())
                        .param("nickname", "nick")
                        .param("email", "email@naver.com")
                        .param("password", "a151385wa3!")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"))
                .andExpect(unauthenticated());

        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("회원 가입 처리 - 비밀번호 인코드")
    void signUp_with_correct_input_passwrod_encode() throws Exception{
        mockMvc.perform(post("/sign-up")
                        .with(csrf())
                        .param("nickname", "nick")
                        .param("email", "email@naver.com")
                        .param("password", "a151385wa3!")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"))
                .andExpect(unauthenticated());

        Account account = accountRepository.findByEmail("email@naver.com").get();
        assertNotEquals("a151385wa3!", account.getPassword());
    }

    @Test
    @DisplayName("인증 메일 확인 - 입력 값 오류")
    void checkEmailToken_with_wrong_input() throws Exception {
        mockMvc.perform(get("/check-email-token")
                        .with(csrf())
                        .param("token", "wrong")
                        .param("email", "mail@mail.com")
                )
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("인증 메일 확인 - 입력 값 정상")
    void checkEmailToken_with_correct_input() throws Exception {
        Account account = Account.builder()
                .email("test@email.com")
                .password("12345678")
                .nickname("nick")
                .build();
        account.generateEmailCheckToken();
        accountRepository.save(account);

        mockMvc.perform(get("/check-email-token")
                        .with(csrf())
                        .param("token", account.getEmailCheckToken())
                        .param("email", account.getEmail())
                )
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(unauthenticated());

        Account findAccount = accountRepository.findByEmail(account.getEmail()).get();
        assertTrue(findAccount.isEmailVerified());
        assertNotNull(findAccount.getJoinedAt());
    }
}