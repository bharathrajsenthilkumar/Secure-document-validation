package com.example.securedocumentvalidation.controller;

import com.example.securedocumentvalidation.dto.UserLoginRequest;
import com.example.securedocumentvalidation.dto.UserRegisterRequest;
import com.example.securedocumentvalidation.entity.User;
import com.example.securedocumentvalidation.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    private final UserService service;

    public AuthController(UserService service) {
        this.service = service;
    }

    // ✅ REGISTER
    @PostMapping("/register")
    public User register(@Valid @RequestBody UserRegisterRequest request) {
        return service.registerUser(request);
    }

    // ✅ LOGIN (JWT TOKEN)
    @PostMapping("/login")
    public String login(@Valid @RequestBody UserLoginRequest request) {
        return service.login(request);
    }
}
