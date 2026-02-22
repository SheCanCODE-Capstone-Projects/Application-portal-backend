package com.igirerwanda.application_portal_backend.admin.service;

import com.igirerwanda.application_portal_backend.admin.dto.AdminNotificationDto;

import java.util.List;
import java.util.UUID;

public interface AdminNotificationService {

    // Send to a specific admin by email
    void sendToAdmin(String adminEmail, String title, String message, String type);

    // Broadcast to ALL admins (e.g. new application submitted)
    void broadcastToAllAdmins(String title, String message, String type);

    // Fetch / manage
    List<AdminNotificationDto> getNotifications(String adminEmail);
    List<AdminNotificationDto> getUnreadNotifications(String adminEmail);
    long getUnreadCount(String adminEmail);
    void markAsRead(UUID notificationId, String adminEmail);
    void markAllAsRead(String adminEmail);
}