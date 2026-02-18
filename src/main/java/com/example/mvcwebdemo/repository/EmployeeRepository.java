package com.example.mvcwebdemo.repository;

import com.example.mvcwebdemo.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // ใช้ Standard CRUD ของ JPA ได้เลย ไม่ต้องเขียนเพิ่ม
}