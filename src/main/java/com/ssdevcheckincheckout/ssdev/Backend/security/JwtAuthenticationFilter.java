package com.ssdevcheckincheckout.ssdev.Backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")  
    private String secretKey;  

    private final UserDetailsService userDetailsService; // Inject UserDetailsService
    private static final Logger LOGGER = Logger.getLogger(JwtAuthenticationFilter.class.getName());

    public JwtAuthenticationFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        LOGGER.info("JwtAuthenticationFilter triggered for request: " + request.getRequestURI());
        
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            LOGGER.info("Token found in Authorization header. Extracting JWT...");
            
            try {
                token = token.substring(7); // Remove "Bearer " prefix

                LOGGER.fine("Extracted Token: " + token);

                // Validate the token using the same secret key used in JWT generation
                Key key = Keys.hmacShaKeyFor(secretKey.getBytes());  
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();  

                String username = claims.getSubject();  // Get the username (email)
                LOGGER.info("Successfully extracted email from token: " + username);

                // Load the full UserDetails using UserDetailsService
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // Create authentication token based on the full UserDetails
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);  // Set authentication in security context
                LOGGER.info("Authentication set in SecurityContext for user: " + username);

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Invalid or expired token", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                return;
            }
        } else {
            LOGGER.warning("No Bearer token found in Authorization header");
        }

        filterChain.doFilter(request, response);
    }
}
