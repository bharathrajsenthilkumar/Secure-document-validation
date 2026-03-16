package com.example.securedocumentvalidation.service;

import com.example.securedocumentvalidation.dto.UserLoginRequest;
import com.example.securedocumentvalidation.dto.UserRegisterRequest;
import com.example.securedocumentvalidation.entity.User;

import java.util.List;

public interface UserService {

    User registerUser(UserRegisterRequest request);

    String login(UserLoginRequest request);

    List<User> getAllUsers();

    void deleteUser(Long id);   // 🔥 add this
}
