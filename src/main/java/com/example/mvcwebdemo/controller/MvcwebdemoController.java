package com.example.mvcwebdemo.controller;

import com.example.mvcwebdemo.model.RegistrationForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MvcwebdemoController {

    // หน้า Home
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // หน้า Login (ใช้กับ Spring Security)
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // หน้า Register (GET)
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "registration";
    }

    // หน้า Register (POST)
    @PostMapping("/register")
    public String processRegistration(
            @Valid @ModelAttribute("registrationForm") RegistrationForm form,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            return "registration";
        }

        // ตาม Lab ยังไม่ต้องบันทึก user จริง
        return "redirect:/success";
    }

    // หน้า Success
    @GetMapping("/success")
    public String success() {
        return "success";
    }
}
