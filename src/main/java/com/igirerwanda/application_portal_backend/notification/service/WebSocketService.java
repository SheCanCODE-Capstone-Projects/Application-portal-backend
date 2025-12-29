package com.igirerwanda.application_portal_backend.notification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void broadcastUserUpdate(Object userData) {
        messagingTemplate.convertAndSend("/topic/users", userData);
    }

    public void broadcastApplicationUpdate(Object applicationData) {
        messagingTemplate.convertAndSend("/topic/applications", applicationData);
    }

    public void broadcastApplicationProgress(String userId, Object progressData) {
        messagingTemplate.convertAndSend("/topic/progress/" + userId, progressData);
    }

    public void broadcastToAdmin(String topic, Object data) {
        messagingTemplate.convertAndSend("/topic/admin/" + topic, data);
    }
}