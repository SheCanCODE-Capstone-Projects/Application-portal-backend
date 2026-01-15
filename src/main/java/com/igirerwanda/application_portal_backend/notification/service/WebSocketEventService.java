package com.igirerwanda.application_portal_backend.notification.service;

import com.igirerwanda.application_portal_backend.notification.dto.WebSocketEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WebSocketEventService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Broadcast to all admins
    public void broadcastToAdmins(String eventType, Object data) {
        try {
            WebSocketEvent<?> event = new WebSocketEvent<>(eventType, data);
            messagingTemplate.convertAndSend("/topic/admin", event);
            log.info("Broadcasted {} event to admins", eventType);
        } catch (Exception e) {
            log.error("Failed to broadcast to admins: {}", e.getMessage());
        }
    }

    // Send to specific user
    public void sendToUser(String userEmail, String eventType, Object data) {
        try {
            WebSocketEvent<?> event = new WebSocketEvent<>(eventType, data, userEmail);
            messagingTemplate.convertAndSendToUser(userEmail, "/queue/notifications", event);
            log.info("Sent {} event to user: {}", eventType, userEmail);
        } catch (Exception e) {
            log.error("Failed to send to user {}: {}", userEmail, e.getMessage());
        }
    }

    // Broadcast user events to admins
    public void broadcastUserEvent(String eventType, Object userData) {
        broadcastToAdmins("USER_" + eventType, userData);
    }

    // Broadcast application events
    public void broadcastApplicationEvent(String eventType, Object applicationData, String userEmail) {
        // To admins
        broadcastToAdmins("APPLICATION_" + eventType, applicationData);
        
        // To specific user
        if (userEmail != null) {
            sendToUser(userEmail, "APPLICATION_" + eventType, applicationData);
        }
    }
}