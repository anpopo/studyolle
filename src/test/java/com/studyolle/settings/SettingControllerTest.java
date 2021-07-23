package com.studyolle.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.WithAccount;
import com.studyolle.account.AccountRepository;
import com.studyolle.account.AccountService;
import com.studyolle.domain.Account;
import com.studyolle.domain.Tag;
import com.studyolle.settings.form.TagForm;
import com.studyolle.tag.TagRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.support.AnnotationConfigContextLoaderUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class SettingControllerTest {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper objectMapper;

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

    @WithAccount("anpopo")
    @DisplayName("패스워드 수정 폼")
    @Test
    void updatePassword_form() throws Exception {
        mockMvc.perform(get("/settings/password"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithAccount("anpopo")
    @DisplayName("패스워드 수정 - 입력값 정상")
    @Test
    void updatePassword_success() throws Exception {
        mockMvc.perform(post("/settings/password")
                .param("newPassword", "12341234")
                .param("newPasswordConfirm", "12341234")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/password"))
                .andExpect(flash().attributeExists("message"));

        Account anpopo = accountRepository.findByNickname("anpopo");
        assertTrue(passwordEncoder.matches("12341234", anpopo.getPassword()));
    }

    @WithAccount("anpopo")
    @DisplayName("패스워드 수정 - 입력값 비정상")
    @Test
    void updatePassword_fail() throws Exception {
        mockMvc.perform(post("/settings/password")
                .param("newPassword", "12341234")
                .param("newPasswordConfirm", "111111")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));

    }

    @WithAccount("anpopo")
    @DisplayName("태그 수정 폼")
    @Test
    void updateTagsForm() throws Exception {
        mockMvc.perform(get("/settings/tags"))
                .andExpect(view().name("settings/tags"))
                .andExpect(model().attributeExists("tags"))
                .andExpect(model().attributeExists("whiteList"))
                .andExpect(model().attributeExists("account"));
    }

    @WithAccount("anpopo")
    @DisplayName("계정에 태그 추가")
    @Test
    void addTag() throws Exception {

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post("/settings/tags/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Optional<Tag> newTag = tagRepository.findByTitle(tagForm.getTagTitle());

        assertNotNull(newTag);
        accountRepository.findByNickname("anpopo").getTags().contains(newTag);
    }

    @WithAccount("anpopo")
    @DisplayName("계정에 태그 삭제")
    @Test
    void removeTag() throws Exception {

        Account anpopo = accountRepository.findByNickname("anpopo");
        Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
        accountService.addTag(anpopo, newTag);

        assertTrue(anpopo.getTags().contains(newTag));


        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post("/settings/tags/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(anpopo.getTags().contains(newTag));

    }
}