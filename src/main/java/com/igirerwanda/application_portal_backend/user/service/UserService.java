package com.igirerwanda.application_portal_backend.user.service;

import com.igirerwanda.application_portal_backend.user.dto.UserResponseDto;
import com.igirerwanda.application_portal_backend.user.entity.User;
import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User findById(Long id);
    User findByRegisterId(Long registerId);
    User softDelete(Long id);
    User archive(Long id);

    UserResponseDto getCurrentUserProfile();
    void applyToCohort(Long cohortId);
}