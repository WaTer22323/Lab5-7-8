package com.example.mvcwebdemo;

import com.example.mvcwebdemo.model.Employee;
import com.example.mvcwebdemo.repository.EmployeeRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

// IMPORT STATIC METHODS (สำคัญมาก! ห้ามลืม)
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;    // สำหรับ PUT
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete; // สำหรับ DELETE
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired // ต้องมีบรรทัดนี้ เพื่อให้ใช้ employeeRepository ใน test ข้อ 9 ได้
    private EmployeeRepository employeeRepository;

    // 1. Test List (GET)
    @Test @Order(1) @WithMockUser(roles = "ADMIN")
    public void testListEmployeesPage() throws Exception {
        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/list"))
                .andExpect(model().attributeExists("employees"));
    }

    // 2. Test Add Form (GET)
    @Test @Order(2) @WithMockUser(roles = "ADMIN")
    public void testShowAddForm() throws Exception {
        mockMvc.perform(get("/employees/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/add"));
    }

    // 3. Test Add Success (POST)
    @Test @Order(3) @WithMockUser(roles = "ADMIN")
    public void testAddEmployee() throws Exception {
        mockMvc.perform(post("/employees/add")
                .param("firstName", "Alice")
                .param("lastName", "Wonder")
                .param("email", "alice@company.com")
                .param("dob", "1995-05-20")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employees"));
    }

    // 4. Test Add Fail - No DOB (POST)
    @Test @Order(4) @WithMockUser(roles = "ADMIN")
    public void testAddEmployeeFail_NoDOB() throws Exception {
        mockMvc.perform(post("/employees/add")
                .param("firstName", "Bob")
                .param("lastName", "Builder")
                .param("email", "bob@company.com")
                .param("dob", "")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("employee", "dob"));
    }

    // 5. Test Add Fail - Invalid Domain (POST)
    @Test @Order(5) @WithMockUser(roles = "ADMIN")
    public void testAddEmployeeFail_InvalidDomain() throws Exception {
        mockMvc.perform(post("/employees/add")
                .param("firstName", "Eve")
                .param("email", "eve@gmail.com")
                .param("dob", "1995-05-20")
                .with(csrf()))
                .andExpect(model().attributeHasFieldErrors("employee", "email"));
    }

    // 6. Test Edit Form (GET)
    @Test @Order(6) @WithMockUser(roles = "ADMIN")
    public void testShowEditForm() throws Exception {
        Employee alice = employeeRepository.findAll().stream()
                .filter(e -> e.getEmail().equals("alice@company.com"))
                .findFirst().orElseThrow();

        mockMvc.perform(get("/employees/edit/" + alice.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/edit"));
    }

    // 7. Test Update Success (PUT)
    @Test @Order(7) @WithMockUser(roles = "ADMIN")
    public void testUpdateEmployee() throws Exception {
        Employee alice = employeeRepository.findAll().stream()
                .filter(e -> e.getEmail().equals("alice@company.com"))
                .findFirst().orElseThrow();

        mockMvc.perform(put("/employees/update/" + alice.getId())
                .param("id", String.valueOf(alice.getId()))
                .param("firstName", "Alice")
                .param("lastName", "Smith")
                .param("email", "alice.smith@company.com")
                .param("dob", "1995-05-20")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employees"));
    }

    // 8. Test Delete Success (DELETE)
    @Test @Order(8) @WithMockUser(roles = "ADMIN")
    public void testDeleteEmployee() throws Exception {
        Employee alice = employeeRepository.findAll().stream()
                .filter(e -> e.getFirstName().equals("Alice"))
                .findFirst().orElseThrow();

        mockMvc.perform(delete("/employees/delete/" + alice.getId())
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employees"));
    }

    // 9. Test Update Fail (PUT)
    @Test @Order(9) @WithMockUser(roles = "ADMIN")
    public void testUpdateEmployeeFail_Validation() throws Exception {
        
        Employee temp = new Employee("Temp", "User", "temp@company.com", new java.util.Date());
        employeeRepository.save(temp);

        mockMvc.perform(put("/employees/update/" + temp.getId())
                .param("id", String.valueOf(temp.getId()))
                .param("firstName", "") // ชื่อว่าง (ผิด)
                .param("lastName", "User")
                .param("email", "bad-email") // อีเมลผิด
                .param("dob", "1995-05-20")
                .with(csrf()))
                .andExpect(status().isOk()) // อยู่หน้าเดิม
                .andExpect(view().name("employees/edit"))
                .andExpect(model().attributeHasFieldErrors("employee", "firstName"))
                .andExpect(model().attributeHasFieldErrors("employee", "email"));
    }

    // 10. Test Delete Fail (DELETE)
    @Test
    @Order(10)
    @WithMockUser(roles = "ADMIN")
    public void testDeleteEmployeeFail_NotFound() { // ไม่ต้อง throws Exception แล้ว
        
        // ใช้ assertThrows เพื่อดักจับ Exception ที่ระเบิดออกมา
        Exception exception = assertThrows(Exception.class, () -> {
            mockMvc.perform(delete("/employees/delete/9999")
                    .with(csrf()));
        });

        // แกะดูไส้ในว่าใช่ IllegalArgumentException ที่เราเขียนไว้ใน Controller ไหม
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertTrue(exception.getCause().getMessage().contains("Invalid employee Id:9999"));
    }
}