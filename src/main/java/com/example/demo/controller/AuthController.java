package com.example.demo.controller;

import com.example.demo.controller.resource.UserResource;
import com.example.demo.controller.dto.UserDto;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public UserResource login(@RequestBody UserDto request, HttpSession session) {
            if (request.getEmail() == null || request.getEmail().trim().isEmpty() ||
                request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                logger.error("Invalid email or password");
                return null;
            }
            User user = userService.findByEmailAndPassword(request.getEmail().trim(), request.getPassword());
            if (user == null) {
                logger.error("Invalid email or password");
                return null;
            }
            UserResource userResource = new UserResource(user.getId(),user.getFirstName(),user.getLastName(),user.getEmail());
            session.setAttribute("currentUser", userResource);
            logger.info("Logged in successfully");
            return userResource;
    }

    @PostMapping("/logout")
    public UserResource logout(HttpSession session) {
            UserResource userResource = (UserResource) session.getAttribute("currentUser");
            session.invalidate();
            logger.info("Logged out successfully");
            return userResource;
    }

    @GetMapping("/current")
    public UserResource getCurrentUser(HttpSession session) {
            UserResource userResource = (UserResource) session.getAttribute("currentUser");
            if (userResource == null) {
                logger.error("User not logged in");
                return  null;
            }
            else{
                logger.info("Current user logged in successfully");
                return userResource;
            }
    }
}

