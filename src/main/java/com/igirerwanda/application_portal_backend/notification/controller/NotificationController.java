package com.igirerwanda.application_portal_backend.notification.controller;

import com.igirerwanda.application_portal_backend.common.util.ApiResponse;
import com.igirerwanda.application_portal_backend.common.util.JwtUtil;
import com.igirerwanda.application_portal_backend.notification.dto.NotificationDto;
import com.igirerwanda.application_portal_backend.notification.service.NotificationService;
import com.igirerwanda.application_portal_backend.user.entity.User;
import com.igirerwanda.application_portal_backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification System", description = "Manage user alerts and messages")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;
    private final UserService userService;


    private UUID getActualUserId() {
        UUID registerId = UUID.fromString(jwtUtil.getCurrentUserId());
        User user = userService.findByRegisterId(registerId);
        return user.getId();
    }

    @GetMapping
    @Operation(summary = "Get All Notifications", description = "Retrieves all notifications for the current user, ordered by date.")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getMyNotifications() {
        return ResponseEntity.ok(ApiResponse.success(
                "Notifications retrieved",
                notificationService.getUserNotifications(getActualUserId())
        ));
    }

    @GetMapping("/unread")
    @Operation(summary = "Get Unread Notifications", description = "Retrieves only unread notifications.")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getUnreadNotifications() {
        return ResponseEntity.ok(ApiResponse.success(
                "Unread notifications retrieved",
                notificationService.getUnreadNotifications(getActualUserId())
        ));
    }

    @GetMapping("/unread/count")
    @Operation(summary = "Count Unread", description = "Returns the number of unread notifications.")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount() {
        return ResponseEntity.ok(ApiResponse.success(
                "Count retrieved",
                Map.of("count", notificationService.getUnreadCount(getActualUserId()))
        ));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark One as Read", description = "Marks a specific notification as read.")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable UUID id) {
        notificationService.markAsRead(id, getActualUserId());
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read"));
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark All as Read", description = "Marks all the user's notifications as read.")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        notificationService.markAllAsRead(getActualUserId());
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read"));
    }
}