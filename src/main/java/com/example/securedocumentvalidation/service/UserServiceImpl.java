package com.example.securedocumentvalidation.service;

import com.example.securedocumentvalidation.dto.UserLoginRequest;
import com.example.securedocumentvalidation.dto.UserRegisterRequest;
import com.example.securedocumentvalidation.entity.Role;
import com.example.securedocumentvalidation.entity.User;
import com.example.securedocumentvalidation.repository.UserRepository;
import com.example.securedocumentvalidation.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger =
            LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // ===============================
    // REGISTER
    // ===============================
    @Override
    public User registerUser(UserRegisterRequest request) {

        logger.info("Registering user: {}", request.getUsername());

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Convert String → Enum safely
        try {
            user.setRole(Role.valueOf(request.getRole().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role. Use USER or ADMIN");
        }

        return userRepository.save(user);
    }

    // ===============================
    // LOGIN
    // ===============================
    @Override
    public String login(UserLoginRequest request) {

        logger.info("User attempting login: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.error("Invalid password for user: {}", request.getUsername());
            throw new BadCredentialsException("Invalid credentials");
        }

        // FIXED: pass username + role
        return jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().name()
        );
    }

    // ===============================
    // ADMIN METHODS
    // ===============================
    @Override
    public List<User> getAllUsers() {
        logger.info("Fetching all users from database");
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }

        userRepository.deleteById(id);
        logger.warn("User deleted with id: {}", id);
    }
}
