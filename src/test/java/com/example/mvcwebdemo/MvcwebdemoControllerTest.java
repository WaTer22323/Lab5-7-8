package com.example.mvcwebdemo;

import com.example.mvcwebdemo.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; // เปลี่ยน import
import org.springframework.boot.test.context.SpringBootTest; // เปลี่ยน import
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

// ไม่ต้อง Import WebSecurityConfig แล้ว เพราะ @SpringBootTest โหลดให้เอง

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ใช้ @SpringBootTest แทน @WebMvcTest (เสถียรกว่าสำหรับการทำ Integration Test)
@SpringBootTest
@AutoConfigureMockMvc
public class MvcwebdemoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    // เรา Mock AuthManager เพื่อควบคุมผลลัพธ์การ Login (ผ่าน/ไม่ผ่าน) ได้ดั่งใจ
    @MockBean
    private AuthenticationManager authenticationManager;

    // -----------------------------------------------------------
    // 1. Test หน้า Home (Logic การแยก Role)
    // -----------------------------------------------------------
    
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void home_WhenAdmin_ShouldReturnAdminPage() throws Exception {
        mockMvc.perform(get("/home"))
               .andExpect(status().isOk())
               .andExpect(view().name("admin"))
               .andExpect(model().attribute("username", "admin"));
    }

    @Test
    @WithMockUser(username = "staff", roles = "STAFF")
    public void home_WhenStaff_ShouldReturnViewerPage() throws Exception {
        mockMvc.perform(get("/home"))
               .andExpect(status().isOk())
               .andExpect(view().name("viewer"))
               .andExpect(model().attribute("username", "staff"));
    }

    @Test
    public void home_WhenNotLogin_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/home"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("http://localhost/login"));
    }

    // -----------------------------------------------------------
    // 2. Test หน้า Static ทั่วไป
    // -----------------------------------------------------------

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void adminPage_ShouldReturnAdminView() throws Exception {
        mockMvc.perform(get("/admin"))
               .andExpect(status().isOk())
               .andExpect(view().name("admin"));
    }
    
    @Test
    @WithMockUser(username = "staff", roles = "STAFF")
    public void viewerPage_ShouldReturnViewerView() throws Exception {
        mockMvc.perform(get("/viewer"))
               .andExpect(status().isOk())
               .andExpect(view().name("viewer"));
    }

    // -----------------------------------------------------------
    // 3. Test การ Register
    // -----------------------------------------------------------

    @Test
    public void registerUser_Success_ShouldRedirectToLoginSuccess() throws Exception {
        // จำลองว่า Authenticate ผ่านเสมอ
        Authentication mockAuth = new UsernamePasswordAuthenticationToken("newuser", "pass");
        when(authenticationManager.authenticate(any())).thenReturn(mockAuth);

        mockMvc.perform(post("/register")
                .param("username", "newuser")
                .param("password", "pass")
                .param("role", "USER")
                .with(csrf())) 
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?success"));
    }

    @Test
    public void registerUser_Fail_ShouldRedirectToRegisterError() throws Exception {
        // จำลองว่า Service โยน Error (User ซ้ำ)
        doThrow(new Exception("User exists")).when(userDetailsService)
                .registerUser(anyString(), anyString(), anyString());

        mockMvc.perform(post("/register")
                .param("username", "duplicateUser")
                .param("password", "pass")
                .param("role", "USER")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register?error"));
    }
}