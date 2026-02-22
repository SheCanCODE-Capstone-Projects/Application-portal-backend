package com.igirerwanda.application_portal_backend.admin.controller;

import com.igirerwanda.application_portal_backend.admin.dto.AdminNotificationDto;
import com.igirerwanda.application_portal_backend.admin.service.AdminNotificationService;
import com.igirerwanda.application_portal_backend.common.util.ApiResponse;
import com.igirerwanda.application_portal_backend.common.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/notifications")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Notifications", description = "Admin-scoped notification management")
public class AdminNotificationController {

    private final AdminNotificationService adminNotificationService;
    private final JwtUtil jwtUtil;

    /**
     * JWT stores Register.id — find the AdminUser by email from the security context,
     * since AdminUser is keyed by email and Register is also keyed by email.
     */
    private String getAdminEmail() {
        return jwtUtil.getCurrentUserEmail();
    }

    @GetMapping
    @Operation(summary = "Get all admin notifications")
    public ResponseEntity<ApiResponse<List<AdminNotificationDto>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(
                "Admin notifications retrieved",
                adminNotificationService.getNotifications(getAdminEmail())
        ));
    }

    @GetMapping("/unread")
    @Operation(summary = "Get unread admin notifications")
    public ResponseEntity<ApiResponse<List<AdminNotificationDto>>> getUnread() {
        return ResponseEntity.ok(ApiResponse.success(
                "Unread admin notifications retrieved",
                adminNotificationService.getUnreadNotifications(getAdminEmail())
        ));
    }

    @GetMapping("/unread/count")
    @Operation(summary = "Count unread admin notifications")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount() {
        return ResponseEntity.ok(ApiResponse.success(
                "Count retrieved",
                Map.of("count", adminNotificationService.getUnreadCount(getAdminEmail()))
        ));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark one admin notification as read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable UUID id) {
        adminNotificationService.markAsRead(id, getAdminEmail());
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read"));
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all admin notifications as read")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        adminNotificationService.markAllAsRead(getAdminEmail());
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read"));
    }
}