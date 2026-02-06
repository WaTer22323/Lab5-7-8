package com.example.mvcwebdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((requests) -> requests
                // *** จุดสำคัญ: อนุญาตให้เข้าหน้า /register และ /login ได้โดยไม่ต้องล็อกอิน ***
                .requestMatchers("/register", "/login", "/css/**", "/js/**").permitAll()
                // หน้าอื่นๆ (เช่น /greet) ต้องล็อกอินก่อนถึงจะเข้าได้
                .anyRequest().authenticated()
            )
            .formLogin((form) -> form
                .loginPage("/login") // บอก Spring ว่าเรามีหน้า Login ที่ทำเองอยู่ที่ path นี้
                .defaultSuccessUrl("/greet", true) // ล็อกอินสำเร็จ ให้เด้งไปหน้า /greet เสมอ
                .permitAll()
            )
            .logout((logout) -> logout
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // ใช้การเข้ารหัสรหัสผ่านแบบ BCrypt (มาตรฐาน)
        return new BCryptPasswordEncoder();
    }
}