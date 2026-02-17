package com.example.mvcwebdemo.repository;

import com.example.mvcwebdemo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// สืบทอด JpaRepository เพื่อให้ได้คำสั่ง save, findAll, delete ฟรีๆ
public interface UserRepository extends JpaRepository<User, Long> {
    // สร้าง Method ค้นหาตาม username (Spring จะสร้าง SQL: SELECT * FROM users WHERE username = ?)
    Optional<User> findByUsername(String username);
}