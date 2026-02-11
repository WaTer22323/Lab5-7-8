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

    // เปลี่ยนจาก Map<String, String> เป็น Map<String, User> เพื่อเก็บข้อมูลทั้งหมด
    private final Map<String, User> users = new HashMap<>();
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(User user) {
        // เข้ารหัสรหัสผ่านก่อนเก็บ
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // บันทึก User ทั้งก้อนลง Map
        users.put(user.getUsername(), user);
    }

    // เพิ่มฟังก์ชันสำหรับดึงข้อมูล User ไปโชว์หน้าเว็บ
    public User getUser(String username) {
        return users.get(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = users.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}