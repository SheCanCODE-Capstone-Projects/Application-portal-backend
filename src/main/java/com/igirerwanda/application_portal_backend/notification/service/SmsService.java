package com.igirerwanda.application_portal_backend.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SmsService {

    /**
     * Sends an SMS message to a specific phone number.
     * In production, integrate with a provider like Twilio or Africa's Talking.
     */
    public void sendSms(String phoneNumber, String message) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            log.warn("Cannot send SMS: No phone number provided.");
            return;
        }

        // Mocking the SMS sending process
        log.info("Sending SMS to {}: {}", phoneNumber, message);

        // Example logic for later:
        // provider.send(phoneNumber, message);
    }
}