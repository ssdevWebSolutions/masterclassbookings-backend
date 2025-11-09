package com.ssdevcheckincheckout.ssdev.Backend.security;

import com.ssdevcheckincheckout.ssdev.Backend.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.logging.Logger;

@Component
public class JWTUtil {

    @Value("${jwt.secret}")  // Inject the secret key from application.properties
    private String secretKey;  // This will hold the secret key value
    
    private static final Logger LOGGER = Logger.getLogger(JWTUtil.class.getName());

    // Generate JWT token
    public String generateToken(User user) {
        Key key = io.jsonwebtoken.security.Keys.hmacShaKeyFor(secretKey.getBytes());  // Use the injected secret key for signing the token
        
        LOGGER.info("Generating token with secret key: " + secretKey); 
        return Jwts.builder()
                .setSubject(user.getEmail())  // Set the subject (email of the user)
                .claim("role", user.getRole())  // Add custom claim (user's role)
                .setIssuedAt(new Date())  // Set the issue date
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))  // Set the expiration time (10 hours)
                .signWith(key, SignatureAlgorithm.HS256)  // Sign the token with HS256 algorithm and key
                .compact();  // Generate and return the token
    }

    // Parse JWT token and extract claims
    public Claims parseToken(String token) {
        Key key = io.jsonwebtoken.security.Keys.hmacShaKeyFor(secretKey.getBytes());  // Use the injected secret key for validation
        LOGGER.info("Parsing token with secret key: " + secretKey); // Log the secret key used for parsing

        return Jwts.parserBuilder()
                .setSigningKey(key)  // Use the same key to validate the token
                .build()
                .parseClaimsJws(token)  // Parse the JWT token and get the claims
                .getBody();  // Return the claims (token body)
    }

    // Extract the email (subject) from the token
    public String extractEmail(String token) {
        return parseToken(token).getSubject();  // Extract the email from the claims
    }

    // Check if the token has expired
    public boolean isTokenExpired(String token) {
    	
        return parseToken(token).getExpiration().before(new Date());  // Check if the expiration date is before the current date
    }

    // Validate the token by checking its signature and expiration
    public boolean validateToken(String token) {
    	System.out.print("hello");
        return !isTokenExpired(token);  // Validate if the token is not expired
    }
    
    
    
//    To generate access token and refresh token 
    public String generateAccessToken(User user) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // 30 mins
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(User user) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)) // 7 days
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

}
