package com.igirerwanda.application_portal_backend.notification.service;

import com.igirerwanda.application_portal_backend.auth.entity.Register;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class EmailService {

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.from.name:Application Portal}")
    private String fromName;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    public void sendEmail(String to, String subject, String htmlContent) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);

            Map<String, Object> payload = new HashMap<>();

            payload.put("sender", Map.of(
                    "email", fromEmail,
                    "name", fromName
            ));

            payload.put("to", List.of(
                    Map.of("email", to)
            ));

            payload.put("subject", subject);
            payload.put("htmlContent", htmlContent);

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    BREVO_API_URL,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Email sent to {}", to);
            } else {
                log.error("Brevo error: {}", response.getBody());
            }

        } catch (Exception e) {
            log.error("Failed to send email to {}", to, e);
        }
    }

    public void sendVerificationEmail(Register user, String token) {
        // Ensure base URL ends with /
        String baseUrl = frontendBaseUrl.endsWith("/")
                ? frontendBaseUrl
                : frontendBaseUrl + "/";

        // FIXED: Changed from "?token=" to direct path "/"
        String link = baseUrl + "verify-email/" + token;

        String html = """
                <html>
                <body style="font-family: Arial, sans-serif;">
                    <div style="max-width:600px;margin:auto;padding:20px;border:1px solid #ddd;">
                        <h2 style="color:#4CAF50;">Email Verification</h2>
                        <p>Hello <strong>%s</strong>,</p>
                        <p>Please verify your email by clicking the button below:</p>
                        <div style="text-align:center;margin:20px;">
                            <a href="%s"
                               style="background:#4CAF50;color:#fff;padding:12px 25px;
                                      text-decoration:none;border-radius:5px;">
                               Verify Email
                            </a>
                        </div>
                        <p>If the button doesn’t work, copy this link:</p>
                        <p>%s</p>
                        <small>This link expires in 24 hours.</small>
                    </div>
                </body>
                </html>
                """.formatted(user.getUsername(), link, link);

        sendEmail(user.getEmail(), "Verify Your Email", html);
    }
}