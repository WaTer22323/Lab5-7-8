package com.example.mvcwebdemo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.mvcwebdemo.model.User;
import com.example.mvcwebdemo.service.CustomUserDetailsService;

@Controller
public class GreetingController {

    private final CustomUserDetailsService userDetailsService;

    public GreetingController(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // หน้าแสดงฟอร์มลงทะเบียน
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // รับค่าจากการลงทะเบียน
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        userDetailsService.registerUser(user);
        return "redirect:/login"; // สมัครเสร็จเด้งไปหน้า Login
    }

    // หน้า Login (Spring Security จะจัดการการ POST ให้เอง)
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // หน้า Greet (เข้าได้เฉพาะตอนล็อกอินแล้ว)
    @GetMapping("/greet")
    public String greet(Authentication authentication, Model model) {
        // ดึงชื่อ User ที่ล็อกอินอยู่มาแสดง
        model.addAttribute("username", authentication.getName());
        return "greet";
    }
}