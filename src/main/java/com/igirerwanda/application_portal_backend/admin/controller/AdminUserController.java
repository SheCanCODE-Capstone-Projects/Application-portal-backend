package com.igirerwanda.application_portal_backend.admin.controller;

import com.igirerwanda.application_portal_backend.user.dto.UserResponseDto;
import com.igirerwanda.application_portal_backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAll() {

        return ResponseEntity.ok(userService.getAllUsersDetailed());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        userService.softDelete(id);
        return ResponseEntity.ok("User soft-deleted");
    }
}