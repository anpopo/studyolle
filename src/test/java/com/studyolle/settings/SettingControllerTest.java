package com.studyolle.settings;

import com.studyolle.WithAccount;
import com.studyolle.account.AccountRepository;
import com.studyolle.account.AccountService;
import com.studyolle.account.SignUpForm;
import com.studyolle.domain.Account;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingControllerTest {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }


    @WithAccount("anpopo")
    @DisplayName("프로필 수정 폼")
    @Test
    void updateProfileForm() throws Exception {

        String bio = "짧은 소개 수정";
        mockMvc.perform(get("/settings/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/profile"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithAccount("anpopo")
    @DisplayName("프로필 수정 하기 - 입력값 정상")
    @Test
    void updateProfile() throws Exception {

        String bio = "짧은 소개 수정";
        mockMvc.perform(post("/settings/profile")
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/profile"))
                .andExpect(flash().attributeExists("message"));


        Account anpopo = accountRepository.findByNickname("anpopo");
        Assertions.assertEquals(bio, anpopo.getBio());
    }


    @WithAccount("anpopo")
    @DisplayName("프로필 수정 하기 - 입력값 에러")
    @Test
    void updateProfile_fail() throws Exception {

        String bio = "짧은 소개 수정짧은 소개 수정짧은 소개 수정짧은 소개 수정짧은 소개 수정짧은 소개 수정짧은 소개 수정짧은 소개 수정짧은 소개 수정짧은 소개 수정짧은 소개 수정짧은 소개 수정짧은 소개 수정짧은 소개 수정짧은 소개 수정짧은 소개 수정짧은 소개 수정짧은 소개 수정짧은 소개 수정";
        mockMvc.perform(post("/settings/profile")
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/profile"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());


        Account anpopo = accountRepository.findByNickname("anpopo");
        Assertions.assertNull(anpopo.getBio());
    }
}