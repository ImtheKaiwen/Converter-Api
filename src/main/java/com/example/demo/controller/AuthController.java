package com.example.demo.controller;

import com.example.demo.model.CurrentUser;
import com.example.demo.model.LoginRequest;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Cookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        try {
            System.out.println("LOGIN REQUEST");
            System.out.println("Session ID: " + session.getId());
            System.out.println("Session is new: " + session.isNew());
            System.out.println("Email: " + request.getEmail());
            

            if (request.getEmail() == null || request.getEmail().trim().isEmpty() ||
                request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Email and password are required", "success", false));
            }

            User user = userService.findByEmailAndPassword(request.getEmail().trim(), request.getPassword());
            if (user == null) {
                System.out.println("User not found in database for email: " + request.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Invalid email or password", "success", false));
            }

            CurrentUser currentUser = new CurrentUser(user.getEmail(),user.getFirstName(),user.getLastName(),user.getId());
            session.setAttribute("currentUser", currentUser);
            
            System.out.println("User logged in successfully: " + user.getEmail());
            System.out.println("Session after login - ID: " + session.getId());
            System.out.println("CurrentUser stored in session: " + currentUser);
            
            return ResponseEntity.ok(Map.of("success", true, "user", user));
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Login failed", "success", false));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        try{
            CurrentUser currentUser = (CurrentUser) session.getAttribute("currentUser");
            System.out.println("User logged out : " + (currentUser != null ? currentUser.getEmail() : "null"));
            session.invalidate();
            System.out.println("Session invalidated successfully");
            return ResponseEntity.ok(Map.of("message", "Successfully logged out", "success", true));
        }catch (Exception e){
            System.err.println("Logout error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Logout failed", "success", false));
        }
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        try{
            System.out.println("=== CURRENT USER REQUEST ===");
            System.out.println("Session ID: " + session.getId());
            System.out.println("Session is new: " + session.isNew());
            
            CurrentUser currentUser = (CurrentUser) session.getAttribute("currentUser");
            System.out.println("CurrentUser from session: " + currentUser);
            
            if (currentUser == null) {
                System.out.println("Kullanıcı session'da bulunamadı");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "User not found", "success", false));
            }
            else{
                System.out.println("Kullanıcı session'da bulundu: " + currentUser.getEmail());
                return ResponseEntity.ok(Map.of("user", currentUser, "success", true));
            }
        }catch (Exception e){
            System.err.println("getCurrentUser error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Control failed", "success", false));
        }
    }
}

