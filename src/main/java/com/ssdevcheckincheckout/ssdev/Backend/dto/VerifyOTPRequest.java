package com.ssdevcheckincheckout.ssdev.Backend.dto;

public class VerifyOTPRequest {
    private String email;
    private String otpCode;

    public VerifyOTPRequest() {}

    public VerifyOTPRequest(String email, String otpCode) {
        this.email = email;
        this.otpCode = otpCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }
}
