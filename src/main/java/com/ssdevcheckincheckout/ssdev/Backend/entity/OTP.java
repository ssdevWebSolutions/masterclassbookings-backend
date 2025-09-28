package com.ssdevcheckincheckout.ssdev.Backend.entity;





import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "otps")
public class OTP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String otpCode;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OTPType otpType;

    @Column(nullable = false)
    private boolean isUsed = false;

    // Public no-arg constructor required by JPA
    public OTP() {
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusMinutes(5); // 5 minutes expiry
    }

    // Constructor with parameters
    public OTP(String email, String otpCode, OTPType otpType) {
        this();
        this.email = email;
        this.otpCode = otpCode;
        this.otpType = otpType;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public OTPType getOtpType() {
        return otpType;
    }

    public void setOtpType(OTPType otpType) {
        this.otpType = otpType;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    // Utility method to check if OTP is expired
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    // Utility method to check if OTP is valid (not used and not expired)
    public boolean isValid() {
        return !isUsed && !isExpired();
    }
}