package com.ssdevcheckincheckout.ssdev.Backend.service;




import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ssdevcheckincheckout.ssdev.Backend.dto.AuthResponse;
import com.ssdevcheckincheckout.ssdev.Backend.dto.LoginRequest;
import com.ssdevcheckincheckout.ssdev.Backend.dto.LoginResponse;
import com.ssdevcheckincheckout.ssdev.Backend.dto.RegisterRequest;
import com.ssdevcheckincheckout.ssdev.Backend.entity.Role;
import com.ssdevcheckincheckout.ssdev.Backend.entity.User;
import com.ssdevcheckincheckout.ssdev.Backend.exceptions.InvalidCredentialsException;
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

        // Determine role (default to USER if null)
        Role userRole = registerRequest.getRole() != null ? registerRequest.getRole() : Role.USER;

        // Create a new user using the builder pattern
        User user = new User.Builder()
                .setEmail(registerRequest.getEmail())
                .setPassword(passwordEncoder.encode(registerRequest.getPassword())) // Encrypt password
                .setFirstName(registerRequest.getFirstName())
                .setLastName(registerRequest.getLastName())
                .setPhoneNumber(registerRequest.getPhoneNumber()) // set phone number
                .setRole(userRole)
                .setEnabled(true) // Active by default
                .build();

        // Save to database
        userRepository.save(user);
    }

    // Login user (authenticate and generate JWT)
    public LoginResponse login(LoginRequest loginRequest) {
        // Find user by email
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        if (userOptional.isEmpty() || !passwordEncoder.matches(loginRequest.getPassword(), userOptional.get().getPassword())) {
        	throw new InvalidCredentialsException("Invalid email or password");
        }

        
        System.out.println(userOptional.get().getRole()+"->");
        // Generate JWT token
        String token = jwtUtil.generateToken(userOptional.get());
        
        
        LoginResponse lr = new LoginResponse();
        Optional<User> getUser = userRepository.findByEmail(userOptional.get().getEmail());
        if(getUser.isPresent())
        {
        	lr.setId(getUser.get().getId());
        }
        else 
        lr.setId(0);
        
        lr.setEmail(userOptional.get().getEmail());
        lr.setFullName(userOptional.get().getFirstName()+" "+ userOptional.get().getLastName());
        lr.setMobileNumber(userOptional.get().getPhoneNumber());
        lr.setRole(userOptional.get().getRole());
        lr.setToken(token);
        
        
 

        // Return AuthResponse with the token
        return lr;
    }
}
