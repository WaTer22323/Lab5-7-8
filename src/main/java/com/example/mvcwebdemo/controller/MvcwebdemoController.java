package com.example.mvcwebdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MvcwebdemoController {

    // เมื่อเข้าหน้าแรก (/) ให้เปิดไฟล์ index.html
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // เมื่อเข้าหน้า /registration ให้เปิดไฟล์ registration.html
    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }
}