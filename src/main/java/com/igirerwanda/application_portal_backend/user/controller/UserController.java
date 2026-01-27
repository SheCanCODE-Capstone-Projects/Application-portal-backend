package com.igirerwanda.application_portal_backend.user.controller;

import com.igirerwanda.application_portal_backend.user.dto.UserResponseDto;
import com.igirerwanda.application_portal_backend.user.entity.User;
import com.igirerwanda.application_portal_backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMyProfile() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    @PostMapping("/apply/{cohortId}")
    public ResponseEntity<?> applyToCohort(@PathVariable UUID cohortId) {
        userService.applyToCohort(cohortId);
        return ResponseEntity.ok(Map.of("message", "Successfully applied to cohort"));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        User user = userService.softDelete(id);
        messagingTemplate.convertAndSend("/topic/admin/user-updates",
                Map.of("action", "DELETE", "userId", id, "status", user.getStatus()));
        return ResponseEntity.ok(Map.of("message", "User soft-deleted successfully"));
    }

    @PatchMapping("/{id}/archive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> archive(@PathVariable UUID id) {
        User user = userService.archive(id);
        messagingTemplate.convertAndSend("/topic/admin/user-updates",
                Map.of("action", "ARCHIVE", "userId", id, "status", user.getStatus()));
        return ResponseEntity.ok(Map.of("message", "User archived successfully"));
    }
}