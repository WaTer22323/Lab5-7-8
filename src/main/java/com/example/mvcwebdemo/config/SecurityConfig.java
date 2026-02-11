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
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((requests) -> requests
                // 1. อนุญาตให้เข้าถึงหน้าเหล่านี้ได้ โดยไม่ต้อง Login
                .requestMatchers("/", "/home", "/register", "/registration", "/css/**", "/js/**", "/images/**").permitAll()
                // 2. หน้าอื่นๆ นอกเหนือจากนี้ ต้อง Login ก่อน
                .anyRequest().authenticated()
            )
            .formLogin((form) -> form
                .loginPage("/login") // กำหนดหน้า Login ของเราเอง
                .defaultSuccessUrl("/greet", true) // Login สำเร็จให้ไปหน้า greet
                .permitAll()
            )
            .logout((logout) -> logout
                .logoutSuccessUrl("/login?logout") // Logout เสร็จกลับมาหน้า Login
                .permitAll()
            );

        return http.build();
    }

    // จำเป็นต้องมี Bean นี้เพื่อให้ CustomUserDetailsService เรียกใช้
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}