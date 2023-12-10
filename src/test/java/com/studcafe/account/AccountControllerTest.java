package com.studcafe.account;

import com.studcafe.account.domain.Account;
import com.studcafe.account.repository.AccountRepository;
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
                .andExpect(model().attributeExists("signUpForm"));
    }

    @Test
    @DisplayName("회원 가입 처리 - csrf 값이 없어 403 에러 발생")
    void signUpSubmit_no_csrf() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "nick")
                .param("email", "email..")
                .param("password", "12345")
        )
        .andExpect(status().isForbidden());
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
                .andExpect(view().name("account/sign-up"));
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
                .andExpect(view().name("redirect:/"));

        System.out.println(accountRepository.findAll().size());
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
                .andExpect(view().name("redirect:/"));

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
                .andExpect(view().name("redirect:/"));

        Account account = accountRepository.findByEmail("email@naver.com").get();
        assertNotEquals("a151385wa3!", account.getPassword());
    }
}