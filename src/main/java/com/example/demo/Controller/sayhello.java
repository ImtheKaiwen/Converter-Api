package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Collections;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3001")
public class sayhello {
    @GetMapping("/hello")
    public Map<String,String> hello(){
        return Collections.singletonMap("message","Hello from backend");
    }
}
