package com.igirerwanda.application_portal_backend.admin.service;

import com.igirerwanda.application_portal_backend.admin.dto.AdminNotificationDto;
import com.igirerwanda.application_portal_backend.admin.entity.AdminNotification;
import com.igirerwanda.application_portal_backend.admin.entity.AdminUser;
import com.igirerwanda.application_portal_backend.admin.repository.AdminNotificationRepository;
import com.igirerwanda.application_portal_backend.admin.repository.AdminUserRepository;
import com.igirerwanda.application_portal_backend.common.exception.NotFoundException;
import com.igirerwanda.application_portal_backend.notification.dto.WebSocketEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminNotificationServiceImpl implements AdminNotificationService {

    private final AdminNotificationRepository adminNotificationRepository;
    private final AdminUserRepository adminUserRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendToAdmin(String adminEmail, String title, String message, String type) {
        AdminNotification notification = save(adminEmail, title, message, type);
        pushWebSocket(adminEmail, notification);
    }

    @Override
    public void broadcastToAllAdmins(String title, String message, String type) {
        // Save a copy for every admin in the system
        List<AdminUser> allAdmins = adminUserRepository.findAll();

        for (AdminUser admin : allAdmins) {
            AdminNotification notification = save(admin.getEmail(), title, message, type);
            pushWebSocket(admin.getEmail(), notification);
        }

        // Also broadcast on the shared admin WebSocket topic so real-time dashboards update
        try {
            messagingTemplate.convertAndSend("/topic/admin/notifications",
                    new WebSocketEvent<>(type, message));
        } catch (Exception e) {
            log.error("Failed to broadcast admin WebSocket notification: {}", e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminNotificationDto> getNotifications(String adminEmail) {
        return adminNotificationRepository
                .findByAdminEmailOrderByCreatedAtDesc(adminEmail)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminNotificationDto> getUnreadNotifications(String adminEmail) {
        return adminNotificationRepository
                .findByAdminEmailAndIsReadFalseOrderByCreatedAtDesc(adminEmail)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(String adminEmail) {
        return adminNotificationRepository.countByAdminEmailAndIsReadFalse(adminEmail);
    }

    @Override
    public void markAsRead(UUID notificationId, String adminEmail) {
        AdminNotification n = adminNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found: " + notificationId));

        // Security: only the owner can mark as read
        if (!n.getAdminEmail().equals(adminEmail)) {
            throw new SecurityException("Access denied to notification: " + notificationId);
        }

        n.setRead(true);
        n.setReadAt(LocalDateTime.now());
        adminNotificationRepository.save(n);
    }

    @Override
    public void markAllAsRead(String adminEmail) {
        List<AdminNotification> unread = adminNotificationRepository
                .findByAdminEmailAndIsReadFalseOrderByCreatedAtDesc(adminEmail);

        unread.forEach(n -> {
            n.setRead(true);
            n.setReadAt(LocalDateTime.now());
        });

        adminNotificationRepository.saveAll(unread);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private AdminNotification save(String adminEmail, String title, String message, String type) {
        AdminNotification n = new AdminNotification();
        n.setAdminEmail(adminEmail);
        n.setTitle(title);
        n.setMessage(message);
        n.setType(type);
        n.setRead(false);
        return adminNotificationRepository.save(n);
    }

    private void pushWebSocket(String adminEmail, AdminNotification notification) {
        try {
            messagingTemplate.convertAndSendToUser(
                    adminEmail,
                    "/queue/admin-notifications",
                    mapToDto(notification)
            );
        } catch (Exception e) {
            log.error("Failed to push WebSocket notification to admin {}: {}", adminEmail, e.getMessage());
        }
    }

    private AdminNotificationDto mapToDto(AdminNotification n) {
        return new AdminNotificationDto(
                n.getId(),
                n.getTitle(),
                n.getMessage(),
                n.getType(),
                n.isRead(),
                n.getCreatedAt(),
                n.getReadAt()
        );
    }
}