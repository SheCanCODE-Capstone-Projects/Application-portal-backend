package com.igirerwanda.application_portal_backend.user.service;

import com.igirerwanda.application_portal_backend.user.dto.UserResponseDto;
import com.igirerwanda.application_portal_backend.user.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {
    List<User> getAllUsers();
    User findById(UUID id);
    User findByRegisterId(UUID registerId);
    User softDelete(UUID id);
    User archive(UUID id);
    UserResponseDto getCurrentUserProfile();
    void applyToCohort(UUID cohortId);
    List<UserResponseDto> getAllUsersDetailed();
}