package com.ssdevcheckincheckout.ssdev.Backend.controller;

import com.ssdevcheckincheckout.ssdev.Backend.dto.LoginRequest;
import com.ssdevcheckincheckout.ssdev.Backend.dto.LoginResponse;

import org.springframework.security.core.Authentication;
import com.ssdevcheckincheckout.ssdev.Backend.dto.RegisterRequest;
import com.ssdevcheckincheckout.ssdev.Backend.entity.User;
import com.ssdevcheckincheckout.ssdev.Backend.service.AuthService;
import com.ssdevcheckincheckout.ssdev.Backend.dto.AuthResponse;
import com.ssdevcheckincheckout.ssdev.Backend.security.JWTUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest; // Use this import for Spring Boot 3 and above


@CrossOrigin(origins = "https://masterclassbookings-rt5n.vercel.app/,http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
//@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JWTUtil jwtUtil;

    // Manually created constructor
    public AuthController(AuthService authService, JWTUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    
    // Register user
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return ResponseEntity.ok("User registered successfully!");
    }

    // Login user (authenticate and return JWT)
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
//        AuthResponse authResponse = authService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(loginRequest));
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
        System.out.print("hello"+bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
        	System.out.print("hello2"+bearerToken);
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

}
