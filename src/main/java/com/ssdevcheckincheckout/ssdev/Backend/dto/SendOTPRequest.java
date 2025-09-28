package com.ssdevcheckincheckout.ssdev.Backend.dto;

public class SendOTPRequest {
    private String email;
    private String firstName; // Optional for forgot password flow

    public SendOTPRequest() {}

    public SendOTPRequest(String email, String firstName) {
        this.email = email;
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
