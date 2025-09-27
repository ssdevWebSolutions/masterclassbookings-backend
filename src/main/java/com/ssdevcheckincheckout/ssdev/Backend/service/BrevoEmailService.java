package com.ssdevcheckincheckout.ssdev.Backend.service;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class BrevoEmailService {

    @Value("${BREVO_API_KEY}")
    private String apiKey;

    @Value("${BREVO_SENDER_EMAIL}")
    private String senderEmail;

    @Value("${BREVO_SENDER_NAME:Booking Platform}")
    private String senderName;

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper mapper = new ObjectMapper();

    public boolean sendEmail(String toEmail, String subject, String htmlContent) throws Exception {
        // Build JSON payload
        ObjectNode root = mapper.createObjectNode();

        ObjectNode sender = root.putObject("sender");
        sender.put("name", senderName);
        sender.put("email", senderEmail);

        ArrayNode to = root.putArray("to");
        ObjectNode toObj = to.addObject();
        toObj.put("email", toEmail);

        root.put("subject", subject);
        root.put("htmlContent", htmlContent);

        String body = mapper.writeValueAsString(root);

        // Create HTTP request
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .timeout(Duration.ofSeconds(20))
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        int status = resp.statusCode();
        System.out.println("Response: " + resp.body());

        return status >= 200 && status < 300;
    }
}

