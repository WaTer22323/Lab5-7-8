package com.example.mvcwebdemo.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.mvcwebdemo.service.CustomUserDetailsService;

@Controller
public class WebsiteController {

    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    public WebsiteController(CustomUserDetailsService userDetailsService, AuthenticationManager authenticationManager) {
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/home")
    public String homepage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        model.addAttribute("username", username);

        // ดึง Role ของ User
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_STAFF");

        // แยกหน้าตาม Role
        if (role.equals("ROLE_ADMIN")) {
            return "admin";
        } else {
            return "viewer";
        }
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/success")
    public String success() {
        return "success";
    }

    @GetMapping("/employee")
    public String employee(Model model) {
        model.addAttribute("users", userDetailsService.getAllUsers());
        return "employee";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String role) { // รับ Role เพิ่ม

        try {
            userDetailsService.registerUser(username, password, role);
        } catch (Exception userExistsAlready) {
            return "redirect:/register?error";
        }

        // Auto-login หลังสมัคร
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "redirect:/success";
    }
}