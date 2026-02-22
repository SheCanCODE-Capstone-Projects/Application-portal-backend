package com.igirerwanda.application_portal_backend.admin.controller;

import com.igirerwanda.application_portal_backend.me.dto.MESyncDto;
import com.igirerwanda.application_portal_backend.me.entity.MasterUser;
import com.igirerwanda.application_portal_backend.me.service.MasterDataService;
import com.igirerwanda.application_portal_backend.user.dto.UserResponseDto;
import com.igirerwanda.application_portal_backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;
    private final MasterDataService masterDataService;

    // Admin viewing all portal users (now includes provider/role automatically)
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAll() {
        return ResponseEntity.ok(userService.getAllUsersDetailed());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        userService.softDelete(id);
        return ResponseEntity.ok(Map.of("message", "User soft-deleted"));
    }

    // --- NEW: View Synchronized Users Step-by-Step (Paginated List) ---
    // Example usage: GET /api/v1/admin/users/synchronized?page=0&size=10
    @GetMapping("/synchronized")
    public ResponseEntity<Page<MESyncDto>> getSynchronizedUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Fetch records ordered by the time they were synced
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "syncedAt"));
        Page<MasterUser> masterUsers = masterDataService.getSynchronizedUsers(pageable);

        // Map Entities to DTOs for the frontend
        Page<MESyncDto> responsePage = masterUsers.map(this::mapToSyncDto);
        return ResponseEntity.ok(responsePage);
    }

    private MESyncDto mapToSyncDto(MasterUser user) {
        MESyncDto dto = new MESyncDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setOriginSystemId(user.getOriginSystemId());
        dto.setCohortJoined(user.getCohortJoined());
        dto.setApplicationDate(user.getApplicationDate());
        dto.setRole(user.getRole());
        dto.setProvider(user.getProvider());
        dto.setSyncedAt(user.getSyncedAt());
        return dto;
    }
}