package com.ssdevcheckincheckout.ssdev.Backend.service;



import com.ssdevcheckincheckout.ssdev.Backend.entity.OTP;
import com.ssdevcheckincheckout.ssdev.Backend.entity.OTPType;
import com.ssdevcheckincheckout.ssdev.Backend.repository.OTPRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OTPService {

    private final OTPRepository otpRepository;
    private final SendGridEmailService emailService;
    private final SecureRandom secureRandom;

    // Rate limiting constants
    private static final int MAX_OTP_ATTEMPTS_PER_HOUR = 5;
    private static final int OTP_LENGTH = 6;

    public OTPService(OTPRepository otpRepository, SendGridEmailService emailService) {
        this.otpRepository = otpRepository;
        this.emailService = emailService;
        this.secureRandom = new SecureRandom();
    }

    /**
     * Generate and send OTP for registration or forgot password
     */
    @Transactional
    public void generateAndSendOTP(String email, OTPType otpType, String firstName) throws IOException {
        // Rate limiting check
        if (isRateLimited(email, otpType)) {
            throw new RuntimeException("Too many OTP requests. Please try again later.");
        }

        // Delete existing OTPs for this email and type
        otpRepository.deleteOTPsByEmailAndType(email, otpType);

        // Generate new OTP
        String otpCode = generateOTPCode();

        // Save OTP to database
        OTP otp = new OTP(email, otpCode, otpType);
        otpRepository.save(otp);

        // Send OTP via email
        sendOTPEmail(email, otpCode, otpType, firstName);
    }

    /**
     * Verify OTP code
     */
    @Transactional
    public boolean verifyOTP(String email, String otpCode, OTPType otpType) {
        Optional<OTP> otpOptional = otpRepository.findValidOTPByEmailAndCodeAndType(
            email, otpCode, otpType, LocalDateTime.now()
        );

        if (otpOptional.isPresent()) {
            OTP otp = otpOptional.get();
            // Mark OTP as used
            otpRepository.markOTPAsUsed(otp.getId());
            return true;
        }

        return false;
    }

    /**
     * Check if user has valid OTP for given type
     */
    public boolean hasValidOTP(String email, OTPType otpType) {
        Optional<OTP> otpOptional = otpRepository.findLatestValidOTPByEmailAndType(
            email, otpType, LocalDateTime.now()
        );
        return otpOptional.isPresent();
    }

    /**
     * Generate 6-digit OTP code
     */
    private String generateOTPCode() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(secureRandom.nextInt(10));
        }
        return otp.toString();
    }

    /**
     * Check rate limiting
     */
    private boolean isRateLimited(String email, OTPType otpType) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        long recentOTPCount = otpRepository.countOTPsCreatedAfter(email, otpType, oneHourAgo);
        return recentOTPCount >= MAX_OTP_ATTEMPTS_PER_HOUR;
    }

    /**
     * Send OTP via email
     */
    private void sendOTPEmail(String email, String otpCode, OTPType otpType, String firstName) throws IOException {
        String subject;
        String htmlContent;

        if (otpType == OTPType.REGISTRATION) {
            subject = "Complete Your Registration - OTP Verification";
            htmlContent = buildRegistrationOTPEmail(otpCode, firstName);
        } else {
            subject = "Reset Your Password - OTP Verification";
            htmlContent = buildForgotPasswordOTPEmail(otpCode, firstName);
        }

        emailService.sendOTPEmail(email, subject, htmlContent);
    }

    /**
     * Build registration OTP email HTML
     */
    private String buildRegistrationOTPEmail(String otpCode, String firstName) {
        return "<!DOCTYPE html>" +
               "<html lang=\"en\">" +
               "<head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
               "<title>Registration OTP</title>" +
               "<style>" +
               "body { font-family: Arial, sans-serif; background-color: #f4f6f9; margin: 0; padding: 20px; }" +
               ".container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 8px 32px rgba(0,0,0,0.12); }" +
               ".header { background: linear-gradient(135deg, #1a472a 0%, #2d5e3f 100%); color: white; padding: 30px; text-align: center; }" +
               ".content { padding: 30px; text-align: center; }" +
               ".otp-box { background: #f8f9fa; border: 2px dashed #1a472a; border-radius: 8px; padding: 20px; margin: 20px 0; }" +
               ".otp-code { font-size: 32px; font-weight: bold; color: #1a472a; letter-spacing: 8px; margin: 10px 0; }" +
               ".footer { background: #1a472a; color: white; text-align: center; padding: 20px; }" +
               "</style></head>" +
               "<body>" +
               "<div class=\"container\">" +
               "<div class=\"header\">" +
               "<h1>🏏 MasterClass Cricket</h1>" +
               "<p>Complete Your Registration</p>" +
               "</div>" +
               "<div class=\"content\">" +
               "<h2>Hello " + (firstName != null ? firstName : "") + "!</h2>" +
               "<p>Welcome to MasterClass Cricket Academy! To complete your registration, please use the OTP code below:</p>" +
               "<div class=\"otp-box\">" +
               "<p><strong>Your OTP Code:</strong></p>" +
               "<div class=\"otp-code\">" + otpCode + "</div>" +
               "<p style=\"color: #666; font-size: 14px;\">This code will expire in 5 minutes</p>" +
               "</div>" +
               "<p>Enter this code on the registration page to verify your email address.</p>" +
               "<p style=\"color: #e74c3c;\"><strong>Important:</strong> Do not share this code with anyone.</p>" +
               "</div>" +
               "<div class=\"footer\">" +
               "<p>MasterClass Cricket Academy</p>" +
               "<p>Developing Tomorrow's Cricket Stars Today</p>" +
               "</div>" +
               "</div>" +
               "</body>" +
               "</html>";
    }

    /**
     * Build forgot password OTP email HTML
     */
    private String buildForgotPasswordOTPEmail(String otpCode, String firstName) {
        return "<!DOCTYPE html>" +
               "<html lang=\"en\">" +
               "<head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
               "<title>Password Reset OTP</title>" +
               "<style>" +
               "body { font-family: Arial, sans-serif; background-color: #f4f6f9; margin: 0; padding: 20px; }" +
               ".container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 8px 32px rgba(0,0,0,0.12); }" +
               ".header { background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%); color: white; padding: 30px; text-align: center; }" +
               ".content { padding: 30px; text-align: center; }" +
               ".otp-box { background: #f8f9fa; border: 2px dashed #e74c3c; border-radius: 8px; padding: 20px; margin: 20px 0; }" +
               ".otp-code { font-size: 32px; font-weight: bold; color: #e74c3c; letter-spacing: 8px; margin: 10px 0; }" +
               ".footer { background: #1a472a; color: white; text-align: center; padding: 20px; }" +
               "</style></head>" +
               "<body>" +
               "<div class=\"container\">" +
               "<div class=\"header\">" +
               "<h1>🔒 Password Reset</h1>" +
               "<p>MasterClass Cricket Academy</p>" +
               "</div>" +
               "<div class=\"content\">" +
               "<h2>Hello " + (firstName != null ? firstName : "") + "!</h2>" +
               "<p>We received a request to reset your password. Use the OTP code below to proceed:</p>" +
               "<div class=\"otp-box\">" +
               "<p><strong>Your OTP Code:</strong></p>" +
               "<div class=\"otp-code\">" + otpCode + "</div>" +
               "<p style=\"color: #666; font-size: 14px;\">This code will expire in 5 minutes</p>" +
               "</div>" +
               "<p>Enter this code on the password reset page to continue.</p>" +
               "<p style=\"color: #e74c3c;\"><strong>Security Notice:</strong> If you didn't request this, please ignore this email and ensure your account is secure.</p>" +
               "</div>" +
               "<div class=\"footer\">" +
               "<p>MasterClass Cricket Academy</p>" +
               "<p>Developing Tomorrow's Cricket Stars Today</p>" +
               "</div>" +
               "</div>" +
               "</body>" +
               "</html>";
    }

    /**
     * Cleanup expired and used OTPs - runs every hour
     */
    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    @Transactional
    public void cleanupOTPs() {
        otpRepository.deleteExpiredOTPs(LocalDateTime.now());
        otpRepository.deleteUsedOTPs();
    }
}