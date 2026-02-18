package com.example.mvcwebdemo.controller;

import com.example.mvcwebdemo.model.RegistrationForm; // ต้องมี Class นี้
import com.example.mvcwebdemo.service.CustomUserDetailsService;
import jakarta.validation.Valid; // สำคัญสำหรับ Validation
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // สำคัญสำหรับเก็บ Error
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MvcwebdemoController {

    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    public MvcwebdemoController(CustomUserDetailsService userDetailsService, AuthenticationManager authenticationManager) {
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    // ... (ส่วน Admin, Viewer, Employee, Home, Login เหมือนเดิม ไม่ต้องแก้) ...
    @GetMapping("/admin") public String adminPage() { return "admin"; }
    @GetMapping("/viewer") public String viewerPage() { return "viewer"; }
    @GetMapping("/employee") public String employeePage() { return "viewer"; }

    @GetMapping("/home")
    public String homepage(Model model) {
        // ... (โค้ด logic home เดิมของคุณ) ...
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        String username = auth.getName();
        model.addAttribute("username", username);
        
        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_STAFF");

        if (role.equals("ROLE_ADMIN")) {
            return "admin";
        } else {
            return "viewer";
        }
    }

    @GetMapping("/login")
    public String login() { return "login"; }

    // --- ส่วนที่แก้: ให้รองรับ Validation Test Case ---

    @GetMapping("/register")
    public String register(Model model) {
        // ส่ง Form เปล่าไปให้หน้า HTML
        model.addAttribute("registrationForm", new RegistrationForm());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registrationForm") RegistrationForm form,
                               BindingResult bindingResult,
                               Model model) {

        // 1. เพิ่ม Logic เช็ค Gmail (เพื่อให้ Test Case: testRegisterFail_NotGmail ผ่าน)
        if (form.getEmail() != null && !form.getEmail().endsWith("@gmail.com")) {
            bindingResult.rejectValue("email", "error.email", "Must be a Gmail address");
        }

        // 2. ถ้ามี Error (เช่น วันเกิดว่าง, อีเมลผิด) ให้กลับไปหน้าเดิม
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            // Mapping ข้อมูลจาก Form ไปเข้า Service
            // ตั้ง default ถ้าไม่ระบุ
            String password = form.getPassword() != null && !form.getPassword().isEmpty() ? form.getPassword() : "defaultPassword";
            String role = form.getRole() != null && !form.getRole().isEmpty() ? form.getRole() : "USER";
            
            userDetailsService.registerUser(form.getUsername(), password, role);
            
            // Auto Login
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(form.getUsername(), password)
            );
            SecurityContextHolder.getContext().setAuthentication(auth);

            return "redirect:/login?success"; // เปลี่ยนตามต้องการ (โจทย์ Test บอกไป success)
        } catch (Exception e) {
            return "redirect:/register?error";
        }
    }
}