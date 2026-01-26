package com.igirerwanda.application_portal_backend.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketEvent<T> {
    private String eventType;
    private T data;
    private LocalDateTime timestamp;
    private String userId; // For user-specific events
    
    public WebSocketEvent(String eventType, T data) {
        this.eventType = eventType;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }
    
    public WebSocketEvent(String eventType, T data, String userId) {
        this.eventType = eventType;
        this.data = data;
        this.userId = userId;
        this.timestamp = LocalDateTime.now();
    }
}