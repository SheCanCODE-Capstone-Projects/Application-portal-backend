package com.igirerwanda.application_portal_backend.notification.controller;

import com.igirerwanda.application_portal_backend.common.util.ApiResponse;
import com.igirerwanda.application_portal_backend.common.util.JwtUtil;
import com.igirerwanda.application_portal_backend.notification.dto.NotificationDto;
import com.igirerwanda.application_portal_backend.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification System", description = "Manage user alerts and messages")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;

    @GetMapping
    @Operation(summary = "Get All Notifications", description = "Retrieves all notifications for the current user, ordered by date.")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getMyNotifications() {
        Long userId = jwtUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                "Notifications retrieved",
                notificationService.getUserNotifications(userId)
        ));
    }

    @GetMapping("/unread")
    @Operation(summary = "Get Unread Notifications", description = "Retrieves only unread notifications.")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getUnreadNotifications() {
        Long userId = jwtUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                "Unread notifications retrieved",
                notificationService.getUnreadNotifications(userId)
        ));
    }

    @GetMapping("/unread/count")
    @Operation(summary = "Count Unread", description = "Returns the number of unread notifications (useful for badges).")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount() {
        Long userId = jwtUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                "Count retrieved",
                Map.of("count", notificationService.getUnreadCount(userId))
        ));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark One as Read", description = "Marks a specific notification as read.")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        Long userId = jwtUtil.getCurrentUserId();
        notificationService.markAsRead(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read"));
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark All as Read", description = "Marks all the user's notifications as read.")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        Long userId = jwtUtil.getCurrentUserId();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read"));
    }
}