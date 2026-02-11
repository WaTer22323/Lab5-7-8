package com.example.mvcwebdemo.controller;

import com.example.mvcwebdemo.model.RegistrationForm;
import com.example.mvcwebdemo.model.User;
import com.example.mvcwebdemo.service.CustomUserDetailsService;

import jakarta.validation.Valid;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class MvcwebdemoController {

    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    public MvcwebdemoController(CustomUserDetailsService userDetailsService,
                                AuthenticationManager authenticationManager) {
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
    }

    // -------------------------------------------------
    // HOME
    // -------------------------------------------------
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // -------------------------------------------------
    // LOGIN (Spring Security)
    // -------------------------------------------------
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // -------------------------------------------------
    // AFTER LOGIN -> เช็ค ROLE (Lab 8)
    // -------------------------------------------------
    @GetMapping("/home")
    public String afterLogin(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String role = auth.getAuthorities().iterator().next().getAuthority();

        model.addAttribute("username", username);

        if ("ROLE_ADMIN".equals(role)) {
            return "admin";
        }
        return "viewer";
    }

    // -------------------------------------------------
    // REGISTER (Lab 8)
    // -------------------------------------------------
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String role) {

        try {
            userDetailsService.registerUser(username, password, role);
        } catch (Exception e) {
            return "redirect:/register?error";
        }

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
        return "redirect:/login?success";
    }

    // -------------------------------------------------
    // LAB เก่า: RegistrationForm
    // -------------------------------------------------
    @GetMapping("/registration")
    public String showGeneralRegistrationForm(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "registration";
    }

    @PostMapping("/registration")
    public String handleGeneralRegistration(
            @Valid @ModelAttribute RegistrationForm registrationForm,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "registration";
        }

        model.addAttribute("firstName", registrationForm.getFirstName());
        model.addAttribute("lastName", registrationForm.getLastName());
        model.addAttribute("country", registrationForm.getCountry());
        model.addAttribute("dob", registrationForm.getDob());
        model.addAttribute("email", registrationForm.getEmail());

        return "success";
    }
}
