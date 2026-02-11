package com.example.mvcwebdemo.model;

public class User {
    private String username;
    private String password;
    
    // --- เพิ่มส่วนนี้เข้าไปครับ (ข้อมูลจาก Lab เก่า) ---
    private String firstName;
    private String lastName;
    private String email;
    private String country;
    private String dob;
    // ---------------------------------------------

    // Constructor เปล่า (จำเป็นต้องมี)
    public User() {}

    // Constructor พร้อมข้อมูล (เผื่อใช้)
    public User(String username, String password, String firstName, String lastName, String email, String country, String dob) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.country = country;
        this.dob = dob;
    }

    // --- Getter / Setter (สำคัญมาก! ต้องมีครบทุกตัว) ---
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }
}