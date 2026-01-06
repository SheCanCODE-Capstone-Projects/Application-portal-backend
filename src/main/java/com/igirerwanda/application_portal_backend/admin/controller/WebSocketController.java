package com.igirerwanda.application_portal_backend.admin.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/connect")
    @SendTo("/topic/status")
    public String handleConnection(String message) {
        return "Connected: " + message;
    }

    @MessageMapping("/admin/subscribe")
    @SendTo("/topic/admin/notifications")
    public String handleAdminSubscription(String message) {
        return "Admin subscribed: " + message;
    }
}