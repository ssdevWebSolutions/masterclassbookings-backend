package com.ssdevcheckincheckout.ssdev.Backend.controller;



import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssdevcheckincheckout.ssdev.Backend.service.BrevoEmailService;

@RestController
public class EmailTestController {

    private final BrevoEmailService emailService;

    public EmailTestController(BrevoEmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/api/email/test1")
    public String testEmail(@RequestParam String to) {
        try {
            boolean sent = emailService.sendEmail(to, "Booking Confirmed",
                    "<p>Your booking is confirmed ✅</p>");
            return sent ? "Email sent successfully" : "Email failed";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
