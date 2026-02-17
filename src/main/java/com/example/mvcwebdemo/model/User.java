package com.example.mvcwebdemo.model;

import jakarta.persistence.*; // Import JPA

@Entity // 1. บอกว่าเป็น Table ในฐานข้อมูล
@Table(name = "users") // ตั้งชื่อตารางว่า users
public class User {

    @Id // 2. บอกว่าเป็น Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // รันเลข auto (1, 2, 3...)
    private Long id;

    @Column(unique = true, nullable = false) // ห้ามซ้ำ, ห้ามว่าง
    private String username;

    @Column(nullable = false)
    private String password;

    private String role;

    // Constructor เปล่า (JPA บังคับต้องมี)
    public User() {}

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters Setters (เพิ่มของ ID ด้วย)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // ... (Getters Setters เดิม: username, password, role) ...
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}