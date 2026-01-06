package com.igirerwanda.application_portal_backend.user.service;

import com.igirerwanda.application_portal_backend.user.entity.User;
import com.igirerwanda.application_portal_backend.user.dto.UserDto;

public interface UserService {
    User findById(Long id);
    User createUser(UserDto userDto);
    User updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
    boolean existsById(Long id);
}
