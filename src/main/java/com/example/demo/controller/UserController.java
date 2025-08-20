package com.example.demo.controller;

import com.example.demo.controller.resource.UserResource;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("api/users")
public class UserController {
    @Autowired
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping
    public List<User> getAllUsers(){
        logger.info("File download successfully");
        return userService.getAllUsers();
    }

    @PostMapping
    public UserResource createUser(@RequestBody User user){
            User savedUser = userService.saveUser(user);
            UserResource userResource = new UserResource(savedUser.getId(), savedUser.getFirstName(), savedUser.getLastName(), savedUser.getEmail());
            logger.info("File download successfully");
            return userResource;
    }

}
