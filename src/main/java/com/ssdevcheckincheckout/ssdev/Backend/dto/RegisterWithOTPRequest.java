package com.ssdevcheckincheckout.ssdev.Backend.dto;

public class RegisterWithOTPRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String otpCode;

    public RegisterWithOTPRequest() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    // Convert to RegisterRequest for service layer
    public RegisterRequest toRegisterRequest() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail(this.email);
        registerRequest.setPassword(this.password);
        registerRequest.setFirstName(this.firstName);
        registerRequest.setLastName(this.lastName);
        registerRequest.setPhoneNumber(this.phoneNumber);
        return registerRequest;
    }
}
