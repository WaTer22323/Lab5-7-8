package com.example.mvcwebdemo;

import com.example.mvcwebdemo.config.WebSecurityConfig;
import com.example.mvcwebdemo.controller.MvcwebdemoController;
import com.example.mvcwebdemo.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MvcwebdemoController.class)
@Import(WebSecurityConfig.class)
public class WebdemoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    @WithMockUser
    public void testHomePage() throws Exception {
        mockMvc.perform(get("/home")).andExpect(status().isOk());
    }

    @Test
    public void testShowRegistrationForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registrationForm"));
    }

    @Test
    public void testRegisterSuccess() throws Exception {
        mockMvc.perform(post("/register")
                .param("firstName", "John")
                .param("lastName", "Doe")
                .param("country", "Thailand")
                .param("email", "john.doe@gmail.com")
                .param("username", "johndoe")
                .param("dob", "1990-01-01")
                .param("password", "password123")
                .param("role", "STAFF")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?success"));
    }

    // แก้ไข: ส่ง params อื่นๆ ให้ครบ เพื่อเช็คแค่ dob จริงๆ
    @Test
    public void testRegisterFail_NoDOB() throws Exception {
        mockMvc.perform(post("/register")
                .param("firstName", "John")
                .param("lastName", "Doe")
                .param("country", "Thailand") // ส่งค่าถูก
                .param("email", "john.doe@gmail.com") // ส่งค่าถูก
                .param("username", "johndoe") // ส่งค่าถูก
                .param("dob", "") // **ว่าง (ตัวปัญหาที่เราจะเทส)**
                .with(csrf()))
                .andExpect(status().isOk()) // หวังผล 200 (เพราะต้องติด Error NotNull)
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "dob"));
    }

    @Test
    public void testRegisterFail_InvalidEmail() throws Exception {
        mockMvc.perform(post("/register")
                .param("firstName", "John")
                .param("lastName", "Doe")
                .param("country", "Thailand")
                .param("email", "not-an-email") // ผิด
                .param("username", "johndoe")
                .param("dob", "1990-01-01")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("registrationForm", "email"));
    }

    @Test
    public void testRegisterFail_NotGmail() throws Exception {
        mockMvc.perform(post("/register")
                .param("firstName", "John")
                .param("lastName", "Doe")
                .param("country", "Thailand")
                .param("email", "john@yahoo.com") // ผิด (ไม่ใช่ Gmail)
                .param("username", "johndoe")
                .param("dob", "1990-01-01")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("registrationForm", "email"));
    }

    // แก้ไข: ส่ง lastName และ country ไปด้วย เพื่อไม่ให้ error count มั่ว
    @Test
    public void testRegisterFailValidation() throws Exception {
        mockMvc.perform(post("/register")
                .param("firstName", "") // 1. ผิด (ว่าง)
                .param("lastName", "Doe") // ถูก
                .param("country", "Thailand") // ถูก
                .param("email", "bad-email") // 2. ผิด (Format) + 3. ผิด (ไม่ใช่ Gmail)
                .param("username", "johndoe") // ถูก
                .param("dob", "") // 4. ผิด (ว่าง)
                .with(csrf()))
                .andExpect(status().isOk())
                // .andExpect(model().errorCount(3)); // เลขอาจดิ้นได้ (เพราะ email อาจโดน 2 เด้ง)
                // เปลี่ยนมาเช็คว่ามี Error ในฟิลด์ที่ต้องการชัวร์กว่าครับ:
                .andExpect(model().attributeHasFieldErrors("registrationForm", "firstName"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "email"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "dob"));
    }
}