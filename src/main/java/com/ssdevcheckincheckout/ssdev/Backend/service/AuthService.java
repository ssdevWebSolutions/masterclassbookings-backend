package com.ssdevcheckincheckout.ssdev.Backend.service;




import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ssdevcheckincheckout.ssdev.Backend.dto.AuthResponse;
import com.ssdevcheckincheckout.ssdev.Backend.dto.LoginRequest;
import com.ssdevcheckincheckout.ssdev.Backend.dto.RegisterRequest;
import com.ssdevcheckincheckout.ssdev.Backend.entity.Role;
import com.ssdevcheckincheckout.ssdev.Backend.entity.User;
import com.ssdevcheckincheckout.ssdev.Backend.repository.UserRepository;
import com.ssdevcheckincheckout.ssdev.Backend.security.JWTUtil;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
//    private final BCryptPasswordEncoder passwordEncoder;
    private final PasswordEncoder passwordEncoder;

    private final JWTUtil jwtUtil;

    // Constructor injection for dependencies
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

 // Register user
    public void register(RegisterRequest registerRequest) {
        // Check if user already exists
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists!");
        }

        // Create a new user using the builder pattern
        User user = new User.Builder()
                    .setEmail(registerRequest.getEmail())
                    .setPassword(passwordEncoder.encode(registerRequest.getPassword())) // Encrypt password
                    .setRole(Role.USER) // Default role is USER
                    .setEnabled(true) // Active by default
                    .build();

        // Save to database
        userRepository.save(user);
    }

    // Login user (authenticate and generate JWT)
    public AuthResponse login(LoginRequest loginRequest) {
        // Find user by email
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        if (userOptional.isEmpty() || !passwordEncoder.matches(loginRequest.getPassword(), userOptional.get().getPassword())) {
            throw new RuntimeException("Invalid email or password!");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(userOptional.get());

        // Return AuthResponse with the token
        return new AuthResponse(token);
    }
}
