package com.example.securedocumentvalidation.controller;

import com.example.securedocumentvalidation.dto.UserRegisterRequest;
import com.example.securedocumentvalidation.dto.UserResponseDTO;
import com.example.securedocumentvalidation.entity.User;
import com.example.securedocumentvalidation.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final Logger logger =
            LoggerFactory.getLogger(AdminController.class);

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    // 🔥 View all users
    @GetMapping("/users")
    public List<UserResponseDTO> getAllUsers() {

        logger.info("Admin requested all users");

        return userService.getAllUsers()
                .stream()
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getRole().name()
                ))
                .toList();
    }

    // 🔥 Delete user
    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable Long id) {

        logger.warn("Admin deleting user with id: {}", id);

        userService.deleteUser(id);
        return "User deleted successfully";
    }

    // 🔥 Create user
    @PostMapping("/users")
    public String createUser(@RequestBody UserRegisterRequest request) {

        logger.info("Admin creating new user: {}", request.getUsername());

        userService.registerUser(request);
        return "User created successfully";
    }
}
