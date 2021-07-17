package com.studyolle.account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class AccountController {

    @GetMapping("/sign-up")
    public String singUpForm(Model model) {
        model.addAttribute(new SignUpForm());
        return "account/sign-up";
    }
}