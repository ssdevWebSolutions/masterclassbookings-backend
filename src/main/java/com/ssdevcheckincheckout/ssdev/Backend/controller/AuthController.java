package com.ssdevcheckincheckout.ssdev.Backend.controller;

import com.ssdevcheckincheckout.ssdev.Backend.dto.*;
import com.ssdevcheckincheckout.ssdev.Backend.entity.User;
import com.ssdevcheckincheckout.ssdev.Backend.repository.UserRepository;
import com.ssdevcheckincheckout.ssdev.Backend.service.AuthService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

import com.ssdevcheckincheckout.ssdev.Backend.security.JWTUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@CrossOrigin(origins = "https://masterclassbookings-rt5n.vercel.app/,http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JWTUtil jwtUtil;
    
    
    @Autowired
    private UserRepository userRepository;
    
    @Value("${jwt.secret}")  // Inject the secret key from application.properties
    private String secretKey;

    // Constructor
    public AuthController(AuthService authService, JWTUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    // === REGISTRATION FLOW WITH OTP ===
    
    // Step 1: Send OTP for registration
    @PostMapping("/register/send-otp")
    public ResponseEntity<ApiResponse> sendRegistrationOTP(@RequestBody SendOTPRequest request) {
        try {
            // Validate email format
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Email is required"));
            }

            // Check if user already exists
            if (authService.emailExists(request.getEmail())) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("User already exists with this email"));
            }

            // Send OTP
            authService.sendRegistrationOTP(request.getEmail(), request.getFirstName());
            
            return ResponseEntity.ok(
                ApiResponse.success("OTP sent successfully to " + request.getEmail())
            );
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to send OTP. Please try again."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Step 2: Complete registration with OTP verification
    @PostMapping("/register/verify")
    public ResponseEntity<ApiResponse> registerWithOTP(@RequestBody RegisterWithOTPRequest request) {
        try {
            // Basic validation
            if (request.getEmail() == null || request.getOtpCode() == null || 
                request.getPassword() == null || request.getFirstName() == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("All fields are required"));
            }

            // Register user with OTP verification
            authService.registerWithOTP(request.toRegisterRequest(), request.getOtpCode());
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration completed successfully!"));
                
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // === FORGOT PASSWORD FLOW WITH OTP ===
    
    // Step 1: Send OTP for forgot password
    @PostMapping("/forgot-password/send-otp")
    public ResponseEntity<ApiResponse> sendForgotPasswordOTP(@RequestBody SendOTPRequest request) {
        try {
            // Validate email
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Email is required"));
            }

            // Send OTP
            authService.sendForgotPasswordOTP(request.getEmail());
            
            return ResponseEntity.ok(
                ApiResponse.success("Password reset OTP sent to " + request.getEmail())
            );
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to send OTP. Please try again."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Step 2: Verify OTP for forgot password
    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<ApiResponse> verifyForgotPasswordOTP(@RequestBody VerifyOTPRequest request) {
        try {
            // Validate input
            if (request.getEmail() == null || request.getOtpCode() == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Email and OTP code are required"));
            }

            // Verify OTP
            boolean isValid = authService.verifyForgotPasswordOTP(request.getEmail(), request.getOtpCode());
            
            if (isValid) {
                return ResponseEntity.ok(
                    ApiResponse.success("OTP verified successfully. You can now reset your password.")
                );
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid or expired OTP code"));
            }
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Step 3: Reset password with OTP
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            // Validate input
            if (request.getEmail() == null || request.getNewPassword() == null || 
                request.getOtpCode() == null || request.getConfirmPassword() == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("All fields are required"));
            }

            // Check password confirmation
            if (!request.isPasswordMatching()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Password and confirm password do not match"));
            }

            // Reset password
            authService.resetPassword(request.getEmail(), request.getNewPassword(), request.getOtpCode());
            
            return ResponseEntity.ok(
                ApiResponse.success("Password reset successfully! You can now login with your new password.")
            );
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // === EXISTING ENDPOINTS (UNCHANGED) ===
    
    // Legacy register endpoint (for backward compatibility)
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return ResponseEntity.ok("User registered successfully!");
    }

    // Login user (authenticate and return JWT)
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(loginRequest));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> req) {
        String refreshToken = req.get("refreshToken");
        try {
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(refreshToken).getBody();
            String email = claims.getSubject();
            User user = userRepository.findByEmail(email).orElseThrow();
            String newAccessToken = jwtUtil.generateAccessToken(user);
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Refresh token invalid or expired");
        }
    }

    
    
    
    
    
    
    @GetMapping("/test")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> test(@AuthenticationPrincipal UserDetails user) {
        if (user == null) {
            return ResponseEntity.status(401).body("User not authenticated.");
        }

        // If the user is authenticated, return their username
        return ResponseEntity.ok("Hello, " + user.getUsername() + "! You are authenticated.");
    }

    // Test authentication and get user info (requires valid JWT token)
    @GetMapping("/test1")
    public ResponseEntity<String> test1(HttpServletRequest request) {
        // Extract JWT token from the Authorization header
        String token = getJwtFromRequest(request);
        
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Invalid or expired token.");
        }
        
        // If the token is valid, return a success response
        return ResponseEntity.ok("Token is valid. You are authenticated.");
    }

    // Helper method to extract JWT token from request header
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        System.out.print("hello" + bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            System.out.print("hello2" + bearerToken);
            return bearerToken.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }
    
    @GetMapping("/protected")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> protectedEndpoint() {
        return ResponseEntity.ok("Access granted to protected endpoint!");
    }
    
    @GetMapping("/add")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addKid() {
        return ResponseEntity.ok("hello");
    }

    // === UTILITY ENDPOINTS ===
    
    // Check if email exists
    @PostMapping("/check-email")
    public ResponseEntity<ApiResponse> checkEmail(@RequestBody SendOTPRequest request) {
        try {
            boolean exists = authService.emailExists(request.getEmail());
            return ResponseEntity.ok(
                ApiResponse.success("Email check completed", 
                    new EmailCheckResponse(exists))
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Invalid email format"));
        }
    }

    // Inner class for email check response
    public static class EmailCheckResponse {
        private boolean exists;

        public EmailCheckResponse(boolean exists) {
            this.exists = exists;
        }

        public boolean isExists() {
            return exists;
        }

        public void setExists(boolean exists) {
            this.exists = exists;
        }
    }
}