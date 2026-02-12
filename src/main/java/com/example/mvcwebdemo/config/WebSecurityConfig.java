package com.example.mvcwebdemo.config;

import com.example.mvcwebdemo.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public WebSecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/register", "/login", "/success").permitAll() // เข้าได้ทุกคน
                .requestMatchers("/admin").hasRole("ADMIN")         // เฉพาะ ADMIN
                .requestMatchers("/viewer").hasRole("STAFF")        // เฉพาะ STAFF
                .requestMatchers("/employee").authenticated()        // เข้าได้เมื่อ login แล้ว
                .anyRequest().authenticated()                       // หน้าอื่นต้อง Login
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/home", true) // Login สำเร็จไปที่ /home (Controller จะแยกหน้าให้เอง)
                .permitAll()
            )
            .logout(logout -> logout
                .permitAll()
            );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}