package com.example.mvcwebdemo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.mvcwebdemo.model.RegistrationForm; // Model สำหรับ Login (Lab 7)
import com.example.mvcwebdemo.model.User; // Model แบบฟอร์มทั่วไป (Lab เก่า)
import com.example.mvcwebdemo.service.CustomUserDetailsService;

import jakarta.validation.Valid;

@Controller
public class MvcwebdemoController {

    private final CustomUserDetailsService userDetailsService;

    // Inject Service เข้ามาผ่าน Constructor
    public MvcwebdemoController(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // ---------------------------------------------------------
    // ส่วนที่ 1: หน้าทั่วไป (Home)
    // ---------------------------------------------------------
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // ---------------------------------------------------------
    // ส่วนที่ 2: Spring Security (Login & Register User) -> Lab 7
    // ---------------------------------------------------------

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // หน้า Greet (เข้าได้เมื่อ Login แล้ว)
    @GetMapping("/greet")
    public String greet(Authentication authentication, Model model) {
        // ดึงชื่อ User ที่ล็อกอินอยู่
        String username = authentication.getName();
        
        // ดึงข้อมูล User ตัวเต็มมาจาก Service (ที่แก้ตะกี้)
        User user = userDetailsService.getUser(username);
        
        // ส่งไปที่หน้าเว็บ
        model.addAttribute("user", user);
        
        return "greet";
    }

    // หน้าฟอร์มสมัครสมาชิก (สำหรับ User Login)
    @GetMapping("/register")
    public String showUserRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; // ต้องมีไฟล์ register.html
    }

    // รับค่าสมัครสมาชิก (User Login)
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        userDetailsService.registerUser(user);
        return "redirect:/login"; 
    }

    // ---------------------------------------------------------
    // ส่วนที่ 3: แบบฟอร์มทั่วไป (RegistrationForm) -> Lab เก่า
    // ---------------------------------------------------------

    // ส่งฟอร์มทั่วไป
    @GetMapping("/registration")
    public String showGeneralRegistrationForm(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "registration"; // ต้องมีไฟล์ registration.html
    }

    // *** จุดที่แก้ไข: เปลี่ยน URL เป็น /registration เพื่อไม่ให้ชนกับ /register ด้านบน ***
    @PostMapping("/registration") 
    public String handleGeneralRegistration(@Valid RegistrationForm registrationForm,
                                            BindingResult bindingResult,
                                            Model model) {
        // ถ้ามี Error
        if (bindingResult.hasErrors()) {
            return "registration";
        }

        // ถ้าผ่าน ส่งไปหน้า success
        model.addAttribute("firstName", registrationForm.getFirstName());
        model.addAttribute("lastName", registrationForm.getLastName());
        model.addAttribute("country", registrationForm.getCountry());
        model.addAttribute("dob", registrationForm.getDob());
        model.addAttribute("email", registrationForm.getEmail());

        return "success";
    }
}