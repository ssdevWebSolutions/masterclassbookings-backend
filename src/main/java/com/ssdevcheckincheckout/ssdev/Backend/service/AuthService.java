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
import com.ssdevcheckincheckout.ssdev.Backend.entity.OTPType;
import com.ssdevcheckincheckout.ssdev.Backend.exceptions.InvalidCredentialsException;
import com.ssdevcheckincheckout.ssdev.Backend.repository.UserRepository;
import com.ssdevcheckincheckout.ssdev.Backend.security.JWTUtil;

import java.io.IOException;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final OTPService otpService;

    // Constructor injection for dependencies
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JWTUtil jwtUtil, OTPService otpService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.otpService = otpService;
    }

    // Step 1: Send OTP for registration
    public void sendRegistrationOTP(String email, String firstName) throws IOException {
        // Check if user already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("User already exists with this email!");
        }

        // Generate and send OTP
        otpService.generateAndSendOTP(email, OTPType.REGISTRATION, firstName);
    }

    // Step 2: Verify OTP and complete registration
    public void registerWithOTP(RegisterRequest registerRequest, String otpCode) {
        // Verify OTP first
        boolean otpValid = otpService.verifyOTP(registerRequest.getEmail(), otpCode, OTPType.REGISTRATION);
        if (!otpValid) {
            throw new RuntimeException("Invalid or expired OTP code!");
        }

        // Check if user already exists (double-check)
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists!");
        }

        // Determine role (default to USER if null)
        Role userRole = registerRequest.getRole() != null ? registerRequest.getRole() : Role.USER;

        // Create a new user using the builder pattern
        User user = new User.Builder()
                .setEmail(registerRequest.getEmail())
                .setPassword(passwordEncoder.encode(registerRequest.getPassword()))
                .setFirstName(registerRequest.getFirstName())
                .setLastName(registerRequest.getLastName())
                .setPhoneNumber(registerRequest.getPhoneNumber())
                .setRole(userRole)
                .setEnabled(true)
                .build();

        // Save to database
        userRepository.save(user);
    }

    // Legacy registration method (keep for backward compatibility)
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
                .setPassword(passwordEncoder.encode(registerRequest.getPassword()))
                .setFirstName(registerRequest.getFirstName())
                .setLastName(registerRequest.getLastName())
                .setPhoneNumber(registerRequest.getPhoneNumber())
                .setRole(userRole)
                .setEnabled(true)
                .build();

        // Save to database
        userRepository.save(user);
    }

    // Step 1: Send OTP for forgot password
    public void sendForgotPasswordOTP(String email) throws IOException {
        // Check if user exists
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("No user found with this email address!");
        }

        User user = userOptional.get();
        // Generate and send OTP
        otpService.generateAndSendOTP(email, OTPType.FORGOT_PASSWORD, user.getFirstName());
    }

    // Step 2: Verify OTP for forgot password
    public boolean verifyForgotPasswordOTP(String email, String otpCode) {
        return otpService.verifyOTP(email, otpCode, OTPType.FORGOT_PASSWORD);
    }

 // Updated resetPassword method - remove the double OTP verification
    public void resetPassword(String email, String newPassword, String otpCode) {
        // Don't verify OTP again - it was already verified in the previous step
        // The frontend should only call this after successful OTP verification
        
        // Find user
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found!");
        }

        User user = userOptional.get();
        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // Login user (authenticate and generate JWT) - unchanged
    public LoginResponse login(LoginRequest loginRequest) {
        // Find user by email
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        if (userOptional.isEmpty() || !passwordEncoder.matches(loginRequest.getPassword(), userOptional.get().getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        System.out.println(userOptional.get().getRole() + "->");
        // Generate JWT token
        String token = jwtUtil.generateToken(userOptional.get());

        LoginResponse lr = new LoginResponse();
        Optional<User> getUser = userRepository.findByEmail(userOptional.get().getEmail());
        if (getUser.isPresent()) {
            lr.setId(getUser.get().getId());
        } else {
            lr.setId(0);
        }

        lr.setEmail(userOptional.get().getEmail());
        lr.setFullName(userOptional.get().getFirstName() + " " + userOptional.get().getLastName());
        lr.setMobileNumber(userOptional.get().getPhoneNumber());
        lr.setRole(userOptional.get().getRole());
        lr.setToken(token);

        // Return AuthResponse with the token
        return lr;
    }

    // Utility method to check if email exists
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // Utility method to check if user has valid registration OTP
    public boolean hasValidRegistrationOTP(String email) {
        return otpService.hasValidOTP(email, OTPType.REGISTRATION);
    }

    // Utility method to check if user has valid forgot password OTP
    public boolean hasValidForgotPasswordOTP(String email) {
        return otpService.hasValidOTP(email, OTPType.FORGOT_PASSWORD);
    }
}