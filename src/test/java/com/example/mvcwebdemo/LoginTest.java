package com.example.mvcwebdemo; // เช็ค package ให้ตรงกับของคุณ

import com.example.mvcwebdemo.service.CustomUserDetailsService; // Import Service มาใช้
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

// Imports สำหรับ Spring Security Test
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomUserDetailsService userDetailsService; // 1. Inject Service เข้ามาเพื่อสร้าง User

    // Test 1: เช็คว่าหน้า Login โหลดขึ้นมาได้ปกติไหม
    @Test
    public void loginPage_ShouldLoadSuccessfully() throws Exception {
        mockMvc.perform(get("/login"))
               .andExpect(status().isOk())
               .andExpect(view().name("login"));
    }

    // Test 2: ลอง Login ผิดๆ (ต้องไม่ผ่าน)
    @Test
    public void loginWithWrongPassword_ShouldFail() throws Exception {
        mockMvc.perform(formLogin("/login")
                .user("admlav").password("wrongpass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    // Test 3: ลอง Login ถูกต้อง (ต้องผ่าน)
    @Test
    public void loginWithCorrectCredentials_ShouldSucceed() throws Exception {
        // 2. สร้าง User จำลองขึ้นมาก่อนเทส (เพื่อการันตีว่ามี User นี้แน่ๆ)
        try {
            // สมัครสมาชิก: username, password, role
            userDetailsService.registerUser("admin", "password", "ADMIN");
        } catch (Exception e) {
            // ถ้ามี User อยู่แล้ว (กรณีรันซ้ำ) ก็ไม่เป็นไร ข้ามไปได้เลย
        }

        // 3. สั่ง Login ด้วย User ที่เพิ่งสร้าง
        mockMvc.perform(formLogin("/login")
                .user("admin").password("password")) 
                .andExpect(status().is3xxRedirection()) // ต้องเด้งเปลี่ยนหน้า
                .andExpect(redirectedUrl("/home"))      // ต้องไปหน้า home
                .andExpect(authenticated());            // สถานะต้อง "ล็อกอินแล้ว"
    }
}