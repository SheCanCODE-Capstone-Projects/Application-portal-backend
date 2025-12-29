package com.igirerwanda.application_portal_backend.user.service;

import com.igirerwanda.application_portal_backend.user.dto.UserDto;
import com.igirerwanda.application_portal_backend.user.entity.User;

import java.util.UUID;

public interface UserService {
    User findById(UUID id);
    User createUser(UserDto userDto);
    User updateUser(UUID id, UserDto userDto);
    void deleteUser(UUID id);
    User findByEmail(String email);
}
