package com.igirerwanda.application_portal_backend.user.service;

import com.igirerwanda.application_portal_backend.user.entity.User;

public interface UserService {
    User findById(Long id);
}
