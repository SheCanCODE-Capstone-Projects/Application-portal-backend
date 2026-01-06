package com.igirerwanda.application_portal_backend.notification.controller;

import com.igirerwanda.application_portal_backend.common.util.JwtUtil;
import com.igirerwanda.application_portal_backend.notification.dto.NotificationDto;
import com.igirerwanda.application_portal_backend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getMyNotifications(Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        List<NotificationDto> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications(Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        List<NotificationDto> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Map<String, String>> markAsRead(
            @PathVariable Long notificationId,
            Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        notificationService.markAsRead(notificationId, userId);
        return ResponseEntity.ok(Map.of("message", "Notification marked as read"));
    }

    @PutMapping("/read-all")
    public ResponseEntity<Map<String, String>> markAllAsRead(Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
    }

    private Long getUserIdFromAuth(Authentication auth) {
        return jwtUtil.getCurrentUserId();
    }
}