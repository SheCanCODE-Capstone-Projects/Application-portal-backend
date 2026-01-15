package com.igirerwanda.application_portal_backend.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@Slf4j
public class ResendEmailService {

    @Value("${resend.api.key}")
    private String apiKey;
    
    @Value("${resend.from.email}")
    private String fromEmail;
    
    private final WebClient webClient;
    
    public ResendEmailService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.resend.com")
                .build();
    }
    
    public void sendEmail(String to, String subject, String htmlContent) {
        try {
            String response = webClient.post()
                    .uri("/emails")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(Map.of(
                            "from", fromEmail,
                            "to", to,
                            "subject", subject,
                            "html", htmlContent
                    ))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            log.info("Email sent successfully to: {} - Response: {}", to, response);
        } catch (Exception e) {
            log.error("Failed to send email to: {} - Error: {}", to, e.getMessage());
            // Don't throw exception to prevent breaking business logic
        }
    }
    
    public void sendEmail(String to, String subject, String htmlContent, String textContent) {
        try {
            String response = webClient.post()
                    .uri("/emails")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(Map.of(
                            "from", fromEmail,
                            "to", to,
                            "subject", subject,
                            "html", htmlContent,
                            "text", textContent
                    ))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            log.info("Email sent successfully to: {} - Response: {}", to, response);
        } catch (Exception e) {
            log.error("Failed to send email to: {} - Error: {}", to, e.getMessage());
        }
    }
}