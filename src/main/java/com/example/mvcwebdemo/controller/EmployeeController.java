package com.example.mvcwebdemo.controller;

import com.example.mvcwebdemo.model.Employee;
import com.example.mvcwebdemo.repository.EmployeeRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*; // Import ให้ครบ

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // 1. List
    @GetMapping("")
    public String listEmployees(Model model) {
        model.addAttribute("employees", employeeRepository.findAll());
        return "employees/list";
    }

    // 2. Show Add Form
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "employees/add";
    }

    // 3. Add Process (POST)
    @PostMapping("/add")
    public String addEmployee(@Valid @ModelAttribute("employee") Employee employee,
                              BindingResult result) {
        
        if (employee.getEmail() != null && !employee.getEmail().endsWith("@company.com")) {
            result.rejectValue("email", "error.email", "Email must belong to @company.com");
        }

        if (result.hasErrors()) {
            return "employees/add";
        }

        employeeRepository.save(employee);
        return "redirect:/employees";
    }

    // 4. Show Edit Form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee Id:" + id));
        model.addAttribute("employee", employee);
        return "employees/edit";
    }

    // -------------------------------------------------------
    // จุดที่ต้องแก้เพื่อให้ Test ผ่าน (Update -> PUT)
    // -------------------------------------------------------
    @PutMapping("/update/{id}") // ** ต้องเป็น PutMapping **
    public String updateEmployee(@PathVariable("id") Long id, 
                                 @Valid @ModelAttribute("employee") Employee employee,
                                 BindingResult result) {
        
        if (result.hasErrors()) {
            employee.setId(id);
            return "employees/edit";
        }

        employeeRepository.save(employee);
        return "redirect:/employees";
    }

    // -------------------------------------------------------
    // จุดที่ต้องแก้เพื่อให้ Test ผ่าน (Delete -> DELETE)
    // -------------------------------------------------------
    @DeleteMapping("/delete/{id}") // ** ต้องเป็น DeleteMapping **
    public String deleteEmployee(@PathVariable("id") Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee Id:" + id));
        employeeRepository.delete(employee);
        return "redirect:/employees";
    }
}