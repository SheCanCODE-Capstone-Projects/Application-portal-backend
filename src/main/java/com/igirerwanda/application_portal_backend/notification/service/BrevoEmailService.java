package com.igirerwanda.application_portal_backend.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class BrevoEmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${app.mail.from:derrickmugisha169@gmail.com")
    private String fromEmail;

    @Value("${app.mail.sender-name:Application Portal}")
    private String senderName;

    private final WebClient webClient;

    public BrevoEmailService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.brevo.com/v3")
                .build();
    }

    public void sendEmail(String to, String subject, String htmlContent) {
        try {
            // Construct Brevo-specific payload
            Map<String, Object> body = Map.of(
                    "sender", Map.of("name", senderName, "email", fromEmail),
                    "to", List.of(Map.of("email", to)),
                    "subject", subject,
                    "htmlContent", htmlContent
            );

            String response = webClient.post()
                    .uri("/smtp/email")
                    .header("api-key", apiKey) // Brevo specific header
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Email sent successfully via Brevo API to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email via Brevo API to: {} - Error: {}", to, e.getMessage());
        }
    }
}