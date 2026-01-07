//package com.igirerwanda.application_portal_backend.user.controller;
//
//import com.igirerwanda.application_portal_backend.user.entity.User;
//import com.igirerwanda.application_portal_backend.user.service.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/v1/users")
//@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')")
//public class UserController {
//
//    private final UserService userService;
//    private final SimpMessagingTemplate messagingTemplate;
//
//    @GetMapping
//    public ResponseEntity<List<User>> getAll() {
//        return ResponseEntity.ok(userService.getAllUsers());
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> delete(@PathVariable Long id) {
//        User user = userService.softDelete(id);
//
//
//        messagingTemplate.convertAndSend("/topic/admin/user-updates",
//                Map.of("action", "DELETE", "userId", id, "status", user.getStatus()));
//
//        return ResponseEntity.ok(Map.of("message", "User soft-deleted successfully"));
//    }
//
//    @PatchMapping("/{id}/archive")
//    public ResponseEntity<?> archive(@PathVariable Long id) {
//        User user = userService.archive(id);
//
//        // Real-time Update: Notify admin dashboard of archive status
//        messagingTemplate.convertAndSend("/topic/admin/user-updates",
//                Map.of("action", "ARCHIVE", "userId", id, "status", user.getStatus()));
//
//        return ResponseEntity.ok(Map.of("message", "User archived successfully"));
//    }
//
//    @PostMapping("/apply/{cohortId}")
//    public ResponseEntity<?> applyToCohort(@PathVariable Long cohortId) {
//        userService.applyToCohort(cohortId);
//        return ResponseEntity.ok(Map.of("message", "Successfully applied to cohort"));
//    }
//}