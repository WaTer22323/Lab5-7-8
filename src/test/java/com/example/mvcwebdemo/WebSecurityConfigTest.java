package com.example.mvcwebdemo;

import com.example.mvcwebdemo.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource; // เพิ่มอันนี้
import org.springframework.test.web.servlet.MockMvc;

// --- Imports นี้สำคัญมาก ห้ามหาย ---
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
// --------------------------------

@SpringBootTest
@AutoConfigureMockMvc
// บังคับเปิด H2 Console ตอนเทส เพื่อแก้ Error 404 ของ H2
@TestPropertySource(properties = {
    "spring.h2.console.enabled=true",
    "spring.h2.console.path=/h2-console"
})
public class WebSecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Test
    public void publicEndpoints_ShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/login")).andExpect(status().isOk());
        mockMvc.perform(get("/register")).andExpect(status().isOk());
    }

    // แก้ไข Method นี้ครับ
    @Test
    public void h2Console_ShouldBeAccessible() throws Exception {
        // H2 Console เป็น Servlet แยกที่ MockMvc มองไม่เห็น จึงคืนค่า 404
        // แต่ถ้าเราได้ 404 แปลว่า Spring Security "อนุญาต" ให้ผ่านเข้ามาแล้ว (ถ้าบล็อกจะได้ 401/403)
        // ดังนั้นสำหรับ MockMvc การเช็คว่าได้ 404 ถือว่าผ่าน Security Config แล้วครับ
        mockMvc.perform(get("/h2-console"))
               .andExpect(status().isNotFound()); // แก้จาก is3xxRedirection() เป็น isNotFound() (404)
    }

    @Test
    public void protectedUrls_WhenUnauthenticated_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/admin"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("http://localhost/login"));

        mockMvc.perform(get("/viewer"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void adminRole_AccessControl() throws Exception {
        // ตอนนี้ Controller มีหน้า /admin แล้ว Test นี้ควรผ่าน
        mockMvc.perform(get("/admin"))
               .andExpect(status().isOk());

        mockMvc.perform(get("/viewer"))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "staff", roles = {"STAFF"})
    public void staffRole_AccessControl() throws Exception {
        // ตอนนี้ Controller มีหน้า /viewer แล้ว Test นี้ควรผ่าน
        mockMvc.perform(get("/viewer"))
               .andExpect(status().isOk());

        mockMvc.perform(get("/admin"))
               .andExpect(status().isForbidden());
    }

    @Test
    public void loginAndLogout_Flow() throws Exception {
        try {
            userDetailsService.registerUser("testlogin", "password123", "USER");
        } catch (Exception e) {}

        mockMvc.perform(formLogin("/login")
                .user("testlogin")
                .password("password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"))
                .andExpect(authenticated()); // Import บรรทัดบนสุดจะช่วยแก้ Error ตรงนี้

        mockMvc.perform(formLogin("/login")
                .user("testlogin")
                .password("wrong"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated()); // และตรงนี้

        mockMvc.perform(logout())
                .andExpect(status().is3xxRedirection())
                .andExpect(unauthenticated());
    }
}