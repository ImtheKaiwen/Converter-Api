package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Collections;
import java.util.Map;

public class Sayhello {
    @GetMapping("/hello")
    public Map<String,String> hello(){
        return Collections.singletonMap("message","Hello from backend");
        // return "hello from backend"
    }
}
