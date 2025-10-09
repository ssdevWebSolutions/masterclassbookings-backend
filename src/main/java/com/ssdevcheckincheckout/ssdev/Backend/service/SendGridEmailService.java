package com.ssdevcheckincheckout.ssdev.Backend.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

@Service
public class SendGridEmailService {

    @Value("${SENDGRID_API_KEY}")
    private String sendGridApiKey;

    @Value("${SENDGRID_SENDER_EMAIL}")
    private String fromEmail;

    @Value("${SENDGRID_SENDER_NAME}")
    private String fromName;

    @Value("${server.port:8080}")
    private String serverPort;
    
//    @Value("${app.base-url:http://localhost}")
//    private String baseUrl;
    
    @Value("${app.base-url:https://masterclassbookings-rt5n.vercel.app}")
    private String baseUrl;
    
    

    public void sendBookingConfirmation(
        String parentEmail,
        String parentName,
        String kidName,
        long bookingId,
        double totalAmount,
        List<String> sessionDetails
    ) throws IOException {

        Email from = new Email(fromEmail, fromName);
        Email to = new Email(parentEmail); // You can change this to parentEmail if needed
        String subject = "✅ Booking Confirmation - Masterclass Cricket Academy";
        String parentSubject = "✅ Booking Confirmation - Masterclass Cricket Academy";
        String adminSubject = "📩 New Booking Received - Masterclass Cricket Academy";


        // Determine programme based on year (same logic as EmailService)
        String programme = "";
        String programmeDescription = "";
        
        for (String session : sessionDetails) {
            String[] parts = session.split(" ");
            if (parts.length > 0) {
                String datePart = parts[parts.length - 1];
                int year = Integer.parseInt(datePart.split("-")[0]);
                if (year == 2025) {
                    programme = "Winter Coaching Clinics - Class 1";
                    programmeDescription = "Block 1: Technical Development Programme";
                } else if (year == 2026) {
                    programme = "Winter Coaching Clinics - Class 2";
                    programmeDescription = "Block 2: Game based scenarios, game plan, target practice, learning to bat in different scenarios, field settings repeated for the games";
                }
            }
        }

        // Build session details and clean up null values (same logic as EmailService)
        StringBuilder sessionsBuilder = new StringBuilder();
        for (String session : sessionDetails) {
            String cleanSession = session.replace(" null ", " Class ");
            sessionsBuilder.append("<li style='margin-bottom: 10px; padding: 8px 12px; background: #fafafa; border-left: 4px solid #1a472a; border-radius: 4px;'>")
                          .append(cleanSession)
                          .append("</li>");
        }
        String sessions = sessionsBuilder.toString();

        // Enhanced HTML email template (same as EmailService)
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<!DOCTYPE html>");
        htmlBuilder.append("<html lang=\"en\">");
        htmlBuilder.append("<head>");
        htmlBuilder.append("<meta charset=\"UTF-8\">");
        htmlBuilder.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        htmlBuilder.append("<title>Booking Confirmation</title>");
        htmlBuilder.append("<style>");
        
        // Enhanced CSS with professional color scheme (same as EmailService)
        htmlBuilder.append("* { margin: 0; padding: 0; box-sizing: border-box; }");
        htmlBuilder.append("body { font-family: 'Arial', 'Helvetica Neue', sans-serif; line-height: 1.6; color: #2c3e50; background-color: #f4f6f9; }");
        htmlBuilder.append(".email-wrapper { background-color: #f4f6f9; padding: 20px 0; min-height: 100vh; }");
        htmlBuilder.append(".container { max-width: 680px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 8px 32px rgba(0,0,0,0.12); }");
        
        // Professional header with cricket theme
        htmlBuilder.append(".header { background: linear-gradient(135deg, #1a472a 0%, #2d5e3f 50%, #1a472a 100%); color: white; padding: 40px 30px; text-align: center; position: relative; }");
        htmlBuilder.append(".header::before { content: ''; position: absolute; top: 0; left: 0; right: 0; height: 4px; background: linear-gradient(90deg, #f39c12, #e74c3c, #9b59b6, #3498db, #2ecc71); }");
        
        // Logo section with fallback
        htmlBuilder.append(".logo-section { margin-bottom: 25px; }");
        htmlBuilder.append(".logo-placeholder { width: 90px; height: 90px; background: rgba(255,255,255,0.15); border: 3px solid rgba(255,255,255,0.3); border-radius: 50%; margin: 0 auto; display: flex; align-items: center; justify-content: center; font-size: 36px; font-weight: bold; color: white; backdrop-filter: blur(10px); }");
        htmlBuilder.append(".header h1 { font-size: 32px; margin-bottom: 8px; font-weight: 700; letter-spacing: -0.5px; }");
        htmlBuilder.append(".header .subtitle { font-size: 16px; opacity: 0.9; font-weight: 300; }");
        
        // Content area
        htmlBuilder.append(".content { padding: 45px 35px; }");
        htmlBuilder.append(".greeting { font-size: 20px; color: #1a472a; margin-bottom: 30px; font-weight: 600; }");
        
        // Success message
        htmlBuilder.append(".success-banner { background: linear-gradient(135deg, #d5f4e6 0%, #a8e6cf 100%); padding: 25px; border-radius: 12px; border: 2px solid #2ecc71; margin-bottom: 35px; text-align: center; }");
        htmlBuilder.append(".success-banner .icon { font-size: 48px; margin-bottom: 15px; }");
        htmlBuilder.append(".success-banner h2 { color: #1a472a; font-size: 24px; margin-bottom: 10px; font-weight: 700; }");
        htmlBuilder.append(".success-banner p { color: #2d5e3f; font-size: 16px; margin: 0; }");
        
        // Booking summary card
        htmlBuilder.append(".booking-card { background: #ffffff; border: 1px solid #e8ecf0; border-radius: 12px; padding: 30px; margin-bottom: 35px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }");
        htmlBuilder.append(".card-title { color: #1a472a; font-size: 22px; font-weight: 700; margin-bottom: 25px; display: flex; align-items: center; border-bottom: 2px solid #e8ecf0; padding-bottom: 15px; }");
        htmlBuilder.append(".card-title .icon { margin-right: 12px; font-size: 24px; }");
        
        htmlBuilder.append(".summary-row { display: flex; margin-bottom: 18px; padding: 12px 0; }");
        htmlBuilder.append(".summary-row:not(:last-child) { border-bottom: 1px solid #f1f3f4; }");
        htmlBuilder.append(".summary-label { font-weight: 600; color: #34495e; min-width: 140px; font-size: 15px; }");
        htmlBuilder.append(".summary-value { color: #2c3e50; flex: 1; font-size: 15px; }");
        htmlBuilder.append(".summary-value strong { color: #1a472a; }");
        
        // Programme description
        htmlBuilder.append(".programme-highlight { background: linear-gradient(135deg, #e8f5ff 0%, #d0ebff 100%); padding: 18px; border-radius: 8px; margin-top: 12px; border-left: 4px solid #3498db; }");
        htmlBuilder.append(".programme-highlight p { margin: 0; font-style: italic; color: #2c3e50; font-size: 14px; }");
        
        // Sessions list
        htmlBuilder.append(".sessions-container { background: #fafbfc; border: 1px solid #e1e8ed; border-radius: 8px; padding: 18px; margin-top: 12px; }");
        htmlBuilder.append(".sessions-container ul { list-style: none; padding: 0; margin: 0; }");
        
        // Amount highlighting
        htmlBuilder.append(".amount-badge { background: linear-gradient(135deg, #2ecc71 0%, #27ae60 100%); color: white; font-weight: 700; font-size: 20px; padding: 15px 20px; border-radius: 8px; text-align: center; box-shadow: 0 4px 12px rgba(46, 204, 113, 0.3); }");
        
        // Information sections
        htmlBuilder.append(".info-section { margin-bottom: 35px; }");
        htmlBuilder.append(".section-header { color: #1a472a; font-size: 20px; font-weight: 700; margin-bottom: 18px; display: flex; align-items: center; }");
        htmlBuilder.append(".section-header .icon { margin-right: 12px; font-size: 22px; }");
        
        htmlBuilder.append(".info-card { background: #ffffff; border: 1px solid #e8ecf0; border-radius: 10px; padding: 25px; }");
        htmlBuilder.append(".info-list { list-style: none; padding: 0; margin: 0; }");
        htmlBuilder.append(".info-list li { padding: 10px 0; padding-left: 30px; position: relative; color: #34495e; font-size: 15px; }");
        htmlBuilder.append(".info-list li::before { content: '✓'; position: absolute; left: 0; color: #2ecc71; font-weight: bold; font-size: 16px; }");
        
        // Day info
        htmlBuilder.append(".day-info { background: linear-gradient(135deg, #fff3cd 0%, #ffeaa7 100%); padding: 20px; border-radius: 10px; border-left: 4px solid #f39c12; }");
        htmlBuilder.append(".day-info strong { color: #d68910; display: block; font-size: 16px; margin-bottom: 8px; }");
        htmlBuilder.append(".day-info p { margin: 0; color: #5d4e37; }");
        
        // Contact warning
        htmlBuilder.append(".contact-alert { background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%); border: 2px solid #ef4444; border-radius: 10px; padding: 25px; margin: 30px 0; text-align: center; }");
        htmlBuilder.append(".contact-alert .alert-title { color: #dc2626; font-weight: 700; font-size: 18px; margin-bottom: 12px; }");
        htmlBuilder.append(".contact-alert p { color: #7f1d1d; margin: 8px 0; }");
        htmlBuilder.append(".contact-btn { background: #1a472a; color: white; padding: 14px 28px; border-radius: 25px; text-decoration: none; display: inline-block; font-weight: 600; margin-top: 15px; transition: background 0.3s ease; }");
        
        // Policy section
        htmlBuilder.append(".policy-section { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 12px; padding: 30px; margin-top: 25px; }");
        htmlBuilder.append(".policy-intro { color: #64748b; margin-bottom: 25px; font-size: 15px; }");
        htmlBuilder.append(".policy-grid { display: grid; gap: 20px; }");
        htmlBuilder.append(".policy-item { background: white; border: 1px solid #e2e8f0; border-radius: 8px; padding: 20px; }");
        htmlBuilder.append(".policy-item h4 { color: #1a472a; margin-bottom: 12px; font-size: 16px; font-weight: 600; }");
        htmlBuilder.append(".policy-item p { color: #475569; margin: 6px 0; font-size: 14px; }");
        htmlBuilder.append(".policy-item p:last-child { margin-bottom: 0; }");
        
        // Footer
        htmlBuilder.append(".footer { background: linear-gradient(135deg, #1a472a 0%, #2d5e3f 100%); color: white; text-align: center; padding: 40px 30px; }");
        htmlBuilder.append(".footer-brand { font-size: 26px; font-weight: 700; margin-bottom: 12px; color: #ffffff; }");
        htmlBuilder.append(".footer-website { color: #a8e6cf; text-decoration: none; font-size: 16px; font-weight: 500; }");
        htmlBuilder.append(".footer-tagline { margin-top: 18px; font-style: italic; opacity: 0.85; font-size: 15px; color: #d5f4e6; }");
        
        // Responsive design
        htmlBuilder.append("@media (max-width: 600px) {");
        htmlBuilder.append(".container { margin: 10px; border-radius: 8px; }");
        htmlBuilder.append(".content { padding: 30px 20px; }");
        htmlBuilder.append(".header { padding: 30px 20px; }");
        htmlBuilder.append(".booking-card { padding: 20px; }");
        htmlBuilder.append(".summary-row { flex-direction: column; }");
        htmlBuilder.append(".summary-label { min-width: auto; margin-bottom: 5px; }");
        htmlBuilder.append("}");
        
        htmlBuilder.append("</style>");
        htmlBuilder.append("</head>");
        htmlBuilder.append("<body>");
        htmlBuilder.append("<div class=\"email-wrapper\">");
        htmlBuilder.append("<div class=\"container\">");
        
        // Header section
        htmlBuilder.append("<div class=\"header\">");
        htmlBuilder.append("<div class=\"logo-section\">");
        htmlBuilder.append("<div class=\"logo-placeholder\">🏏</div>");
        htmlBuilder.append("</div>");
        htmlBuilder.append("<h1>Booking Confirmed!</h1>");
        htmlBuilder.append("<p class=\"subtitle\">Welcome to Masterclass Cricket Academy</p>");
        htmlBuilder.append("</div>");
        
        // Main content
        htmlBuilder.append("<div class=\"content\">");
        htmlBuilder.append("<div class=\"greeting\">Dear ").append(parentName).append(",</div>");
        
        // Success banner
        htmlBuilder.append("<div class=\"success-banner\">");
        htmlBuilder.append("<div class=\"icon\">🎉</div>");
        htmlBuilder.append("<h2>Booking Successfully Confirmed!</h2>");
        htmlBuilder.append("<p>We're thrilled to welcome your child to our cricket programme. An exciting journey awaits!</p>");
        htmlBuilder.append("</div>");
        
        // Booking summary
        htmlBuilder.append("<div class=\"booking-card\">");
        htmlBuilder.append("<div class=\"card-title\"><span class=\"icon\">📋</span>Booking Details</div>");
        
        htmlBuilder.append("<div class=\"summary-row\">");
        htmlBuilder.append("<div class=\"summary-label\">Child's Name:</div>");
        htmlBuilder.append("<div class=\"summary-value\"><strong>").append(kidName).append("</strong></div>");
        htmlBuilder.append("</div>");
        
        htmlBuilder.append("<div class=\"summary-row\">");
        htmlBuilder.append("<div class=\"summary-label\">Programme:</div>");
        htmlBuilder.append("<div class=\"summary-value\"><strong>").append(programme).append("</strong>");
        if (!programmeDescription.isEmpty()) {
            htmlBuilder.append("<div class=\"programme-highlight\"><p>").append(programmeDescription).append("</p></div>");
        }
        htmlBuilder.append("</div>");
        htmlBuilder.append("</div>");
        
        htmlBuilder.append("<div class=\"summary-row\">");
        htmlBuilder.append("<div class=\"summary-label\">Sessions:</div>");
        htmlBuilder.append("<div class=\"summary-value\">");
        htmlBuilder.append("<div class=\"sessions-container\"><ul>").append(sessions).append("</ul></div>");
        htmlBuilder.append("</div>");
        htmlBuilder.append("</div>");
        
        htmlBuilder.append("<div class=\"summary-row\">");
        htmlBuilder.append("<div class=\"summary-label\">Venue:</div>");
        htmlBuilder.append("<div class=\"summary-value\"><strong>📍 Tiffin Girls School, KT2 5PL</strong></div>");
        htmlBuilder.append("</div>");
        
        htmlBuilder.append("<div class=\"summary-row\">");
        htmlBuilder.append("<div class=\"summary-label\">Amount Paid:</div>");
        htmlBuilder.append("<div class=\"summary-value\">");
        htmlBuilder.append("<div class=\"amount-badge\">£").append(String.format("%.2f", totalAmount)).append("</div>");
        htmlBuilder.append("</div>");
        htmlBuilder.append("</div>");
        
        htmlBuilder.append("<div class=\"summary-row\">");
        htmlBuilder.append("<div class=\"summary-label\">Reference ID:</div>");
        htmlBuilder.append("<div class=\"summary-value\"><strong>#").append(bookingId).append("</strong></div>");
        htmlBuilder.append("</div>");
        htmlBuilder.append("</div>");
        
        // What to bring section
        htmlBuilder.append("<div class=\"info-section\">");
        htmlBuilder.append("<div class=\"section-header\"><span class=\"icon\">🎒</span>What to Bring</div>");
        htmlBuilder.append("<div class=\"info-card\">");
        htmlBuilder.append("<ul class=\"info-list\">");
        htmlBuilder.append("<li>Comfortable sports kit and appropriate footwear</li>");
        htmlBuilder.append("<li>Cricket bat (if available) and protective gear</li>");
        htmlBuilder.append("<li>Water bottle and any necessary medication</li>");
        htmlBuilder.append("<li>Positive attitude and eagerness to learn!</li>");
        htmlBuilder.append("</ul>");
        htmlBuilder.append("</div>");
        htmlBuilder.append("</div>");
        
        // On the day section
        htmlBuilder.append("<div class=\"info-section\">");
        htmlBuilder.append("<div class=\"section-header\"><span class=\"icon\">📅</span>Important Information</div>");
        htmlBuilder.append("<div class=\"day-info\">");
        htmlBuilder.append("<strong>⏰ Please arrive 10 minutes before your session starts</strong>");
        htmlBuilder.append("<p>Our experienced coaches will provide a safe, engaging, and challenging environment tailored to your child's skill level. We look forward to helping them develop their cricket abilities!</p>");
        htmlBuilder.append("</div>");
        htmlBuilder.append("</div>");
        
        // Contact warning
        htmlBuilder.append("<div class=\"contact-alert\">");
        htmlBuilder.append("<div class=\"alert-title\">⚠️ IMPORTANT NOTICE</div>");
        htmlBuilder.append("<p>This mailbox is not monitored. Please do not reply to this email.</p>");
        htmlBuilder.append("<p>For any questions or booking changes, please contact us at:</p>");
        htmlBuilder.append("<a href=\"mailto:admin@masterclasscricket.co.uk\" class=\"contact-btn\">");
        htmlBuilder.append("admin@masterclasscricket.co.uk");
        htmlBuilder.append("</a>");
        htmlBuilder.append("</div>");
        
        // Policy section
        htmlBuilder.append("<div class=\"info-section\">");
        htmlBuilder.append("<div class=\"section-header\"><span class=\"icon\">📜</span>Refund & Cancellation Policy</div>");
        htmlBuilder.append("<p class=\"policy-intro\">");
        htmlBuilder.append("To maintain fairness and programme integrity, Masterclass Cricket Academy operates under the following policy:");
        htmlBuilder.append("</p>");
        
        htmlBuilder.append("<div class=\"policy-section\">");
        htmlBuilder.append("<div class=\"policy-grid\">");
        
        htmlBuilder.append("<div class=\"policy-item\">");
        htmlBuilder.append("<h4>Missed Sessions</h4>");
        htmlBuilder.append("<p>• Credits available for cancellations with 48+ hours notice</p>");
        htmlBuilder.append("<p>• No credits for late cancellations or no-shows</p>");
        htmlBuilder.append("</div>");
        
        htmlBuilder.append("<div class=\"policy-item\">");
        htmlBuilder.append("<h4>Credit Usage</h4>");
        htmlBuilder.append("<p>• Credits apply to 1-to-1 sessions or group activities</p>");
        htmlBuilder.append("<p>• Must be used before 01/09/2026</p>");
        htmlBuilder.append("</div>");
        
        htmlBuilder.append("<div class=\"policy-item\">");
        htmlBuilder.append("<h4>Block Bookings</h4>");
        htmlBuilder.append("<p>• Non-refundable fees</p>");
        htmlBuilder.append("<p>• Exception: Medical exemption with valid documentation</p>");
        htmlBuilder.append("</div>");
        
        htmlBuilder.append("<div class=\"policy-item\">");
        htmlBuilder.append("<h4>General Terms</h4>");
        htmlBuilder.append("<p>• Final decisions rest with Masterclass Cricket Academy</p>");
        htmlBuilder.append("<p>• Enrollment confirms acceptance of this policy</p>");
        htmlBuilder.append("</div>");
        
        htmlBuilder.append("</div>");
        htmlBuilder.append("</div>");
        htmlBuilder.append("</div>");
        
        htmlBuilder.append("</div>");
        
        // Footer
        htmlBuilder.append("<div class=\"footer\">");
        htmlBuilder.append("<div class=\"footer-brand\">🏏 Masterclass Cricket Academy</div>");
        htmlBuilder.append("<a href=\"https://www.masterclasscricket.co.uk\" class=\"footer-website\">www.masterclasscricket.co.uk</a>");
        htmlBuilder.append("<div class=\"footer-tagline\">Developing Tomorrow's Cricket Stars Today</div>");
        htmlBuilder.append("</div>");
        
        htmlBuilder.append("</div>");
        htmlBuilder.append("</div>");
        htmlBuilder.append("</body>");
        htmlBuilder.append("</html>");
        
        
        String htmlContent = htmlBuilder.toString();

        // --- Send to Parent ---
        sendEmailToRecipient(parentEmail, parentSubject, htmlContent);

        // --- Send to Admin ---
        sendEmailToRecipient("admin@masterclasscricket.co.uk", adminSubject, htmlContent);

//        // Create SendGrid mail object
//        Content content = new Content("text/html", htmlBuilder.toString());
//        Mail mail = new Mail(from, subject, to, content);
//
//        // Send the email using SendGrid API
//        SendGrid sg = new SendGrid(sendGridApiKey);
//        Request request = new Request();
//        
//        try {
//            request.setMethod(Method.POST);
//            request.setEndpoint("mail/send");
//            request.setBody(mail.build());
//            Response response = sg.api(request);
//            
//            // Optional: Log the response for debugging
//            System.out.println("SendGrid Response Status Code: " + response.getStatusCode());
//            System.out.println("SendGrid Response Body: " + response.getBody());
//            System.out.println("SendGrid Response Headers: " + response.getHeaders());
//            
//        } catch (IOException ex) {
//            System.err.println("Error sending email via SendGrid: " + ex.getMessage());
//            throw ex;
//        }
    }
    
    
    /** Generic reusable email sender */
    private void sendEmailToRecipient(String recipientEmail, String subject, String htmlContent) throws IOException {
        Email from = new Email(fromEmail, fromName);
        Email to = new Email(recipientEmail);
        Content content = new Content("text/html", htmlContent);

        Mail mail = new Mail(from, subject, to, content);
        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            System.out.println("Email sent to: " + recipientEmail);
            System.out.println("Status Code: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());
        } catch (IOException ex) {
            System.err.println("Error sending email via SendGrid to " + recipientEmail + ": " + ex.getMessage());
            throw ex;
        }
    }
    
    
    /**
     * Send OTP email - new method for OTP functionality
     */
    public void sendOTPEmail(String toEmail, String subject, String htmlContent) throws IOException {
        Email from = new Email(fromEmail, fromName);
        Email to = new Email(toEmail);
        
        // Create SendGrid mail object
        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, to, content);

        // Send the email using SendGrid API
        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            
            // Optional: Log the response for debugging
            System.out.println("SendGrid OTP Email Response Status Code: " + response.getStatusCode());
            System.out.println("SendGrid OTP Email Response Body: " + response.getBody());
            System.out.println("SendGrid OTP Email Response Headers: " + response.getHeaders());
            
        } catch (IOException ex) {
            System.err.println("Error sending OTP email via SendGrid: " + ex.getMessage());
            throw ex;
        }
    }
    
    
    
}