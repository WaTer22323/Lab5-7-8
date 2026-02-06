package com.example.mvcwebdemo.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.mvcwebdemo.model.User;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    // ใช้ Map จำลอง Database เก็บ user
    private final Map<String, String> users = new HashMap<>();
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    // ฟังก์ชันสำหรับลงทะเบียนผู้ใช้ใหม่
    public void registerUser(User user) {
        // เข้ารหัสรหัสผ่านก่อนเก็บ
        users.put(user.getUsername(), passwordEncoder.encode(user.getPassword()));
    }

    // ฟังก์ชันที่ Spring Security ใช้ตรวจสอบตอน Login
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!users.containsKey(username)) {
            throw new UsernameNotFoundException("User not found");
        }
        
        // สร้าง UserDetails object ส่งกลับให้ Spring Security ตรวจสอบ
        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(users.get(username))
                .roles("USER")
                .build();
    }
}