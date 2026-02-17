package com.example.mvcwebdemo; // อย่าลืมเช็คชื่อ package ให้ตรงกับของคุณ

import com.example.mvcwebdemo.service.CustomUserDetailsService;
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
public class LoginTest2 {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomUserDetailsService userDetailsService; // เรียก Service มาใช้งาน

    // Test 1: หน้า Login ต้องโหลดได้
    @Test
    public void test1_LoginPage_ShouldLoad() throws Exception {
        mockMvc.perform(get("/login"))
               .andExpect(status().isOk())
               .andExpect(view().name("login"));
    }

    // Test 2: ล็อกอินผิด ต้องเด้งไปหน้า error
    @Test
    public void test2_LoginWrong_ShouldFail() throws Exception {
        mockMvc.perform(formLogin("/login")
                .user("unknownUser").password("wrongPass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    // Test 3: ล็อกอินถูก (โดยการสมัครสมาชิกให้ก่อนเทส) **นี่คือส่วนที่เพิ่มเข้ามา**
    @Test
    public void test3_LoginCorrect_ShouldSucceed() throws Exception {
        // Step พิเศษ: สร้าง User เตรียมไว้ในระบบก่อนเทส
        // เพราะตอนรัน Test ฐานข้อมูล/Memory จะถูกล้างใหม่หมด
        try {
            userDetailsService.registerUser("testuser", "password123", "STAFF");
        } catch (Exception e) {
            // ถ้ามี user อยู่แล้ว (กรณีรันซ้ำ) ก็ข้ามไป
        }

        // เริ่มการเทส Login
        mockMvc.perform(formLogin("/login")
                .user("testuser").password("password123")) 
                .andExpect(status().is3xxRedirection()) // ต้องเด้งเปลี่ยนหน้า
                .andExpect(redirectedUrl("/home"))      // ต้องไปหน้า /home
                .andExpect(authenticated());            // สถานะต้อง "Login สำเร็จ"
    }
}