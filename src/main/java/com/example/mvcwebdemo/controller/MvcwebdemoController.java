package com.example.mvcwebdemo.controller;

import com.example.mvcwebdemo.service.CustomUserDetailsService;
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

@Controller
public class MvcwebdemoController {

    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    public MvcwebdemoController(CustomUserDetailsService userDetailsService, AuthenticationManager authenticationManager) {
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
    }

    // --- ส่วนที่ต้องเพิ่มเพื่อให้ Test ผ่าน และ WebSecurityConfig ทำงานสมบูรณ์ ---
    @GetMapping("/admin")
    public String adminPage() {
        return "admin"; // ไปที่ไฟล์ admin.html
    }

    @GetMapping("/viewer")
    public String viewerPage() {
        return "viewer"; // ไปที่ไฟล์ viewer.html
    }
    
    @GetMapping("/employee")
    public String employeePage() {
        return "viewer"; // สมมติให้พนักงานทั่วไปใช้หน้า viewer หรือสร้าง employee.html ใหม่ก็ได้
    }
    // -------------------------------------------------------------------

    @GetMapping("/home")
    public String homepage(Model model) {
        // ... (โค้ดเดิมของคุณ) ...
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

    @GetMapping("/register")
    public String register() { return "register"; }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password, @RequestParam String role) {
        try {
            userDetailsService.registerUser(username, password, role);
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
            return "redirect:/login?success";
        } catch (Exception e) {
            return "redirect:/register?error";
        }
    }
}