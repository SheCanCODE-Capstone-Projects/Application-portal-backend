package com.igirerwanda.application_portal_backend.user.service;

import com.igirerwanda.application_portal_backend.common.exception.NotFoundException;
import com.igirerwanda.application_portal_backend.user.entity.User;
import com.igirerwanda.application_portal_backend.auth.repository.RegisterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final RegisterRepository registerRepository;

    @Override
    public User findById(Long id) {
        // For now, create a User from Register entity
        // This is a simplified implementation
        var register = registerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        User user = new User();
        user.setId(id);
        user.setRegister(register);
        return user;
    }
}
