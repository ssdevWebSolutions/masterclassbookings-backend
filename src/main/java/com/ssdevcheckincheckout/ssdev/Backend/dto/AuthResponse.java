package com.ssdevcheckincheckout.ssdev.Backend.dto;



public class AuthResponse {

    private String token;

    // Constructor with a single parameter
    public AuthResponse(String token) {
        this.token = token;
    }

    // Getter for token
    public String getToken() {
        return token;
    }

    // Setter for token
    public void setToken(String token) {
        this.token = token;
    }
}