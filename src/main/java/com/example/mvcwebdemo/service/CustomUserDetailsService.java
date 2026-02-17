package com.example.mvcwebdemo.service;

import com.example.mvcwebdemo.model.User;
import com.example.mvcwebdemo.repository.UserRepository; // Import Repo
import org.springframework.security.core.userdetails.*;
import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    // 1. เรียกใช้ Repository แทน HashMap
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 2. ค้นหาจาก Database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }

    public void registerUser(String username, String password, String role) throws Exception {
        // 3. เช็คว่ามีใน Database หรือยัง
        if (userRepository.findByUsername(username).isPresent()) {
            throw new Exception("User already exists");
        }
        
        // 4. บันทึกลง Database
        String encodedPassword = passwordEncoder.encode(password);
        User newUser = new User(username, encodedPassword, role);
        userRepository.save(newUser); // คำสั่ง Save ลง DB จริง
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}