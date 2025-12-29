package com.igirerwanda.application_portal_backend.user.service;

import com.igirerwanda.application_portal_backend.common.exception.NotFoundException;
import com.igirerwanda.application_portal_backend.user.dto.UserDto;
import com.igirerwanda.application_portal_backend.user.entity.User;
import com.igirerwanda.application_portal_backend.auth.repository.RegisterRepository;
import com.igirerwanda.application_portal_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final RegisterRepository registerRepository;
    private final UserRepository userRepository;

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    @Override
    @Transactional
    public User createUser(UserDto userDto) {
        // Check if user already exists
        if (userRepository.existsByRegisterEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("User with email already exists: " + userDto.getEmail());
        }

        var register = registerRepository.findByEmail(userDto.getEmail())
                .orElseThrow(() -> new NotFoundException("Register record not found for email: " + userDto.getEmail()));

        // Validate register is verified
        if (!register.isEmailVerified()) {
            throw new IllegalStateException("Cannot create user: email not verified for " + userDto.getEmail());
        }

        User user = new User();
        user.setRegister(register);
        
        // Set status properly using enum
        if (userDto.getStatus() != null) {
            user.setStatus(userDto.getStatus());
        }
        // cohort will be set when user applies to a specific cohort
        
        User savedUser = userRepository.save(user);
        
        // Ensure user is immediately available for related processes
        userRepository.flush();
        
        return savedUser;
    }

    @Override
    @Transactional
    public User updateUser(UUID id, UserDto userDto) {
        User user = findById(id);
        
        if (userDto.getStatus() != null) {
            user.setStatus(userDto.getStatus());
        }
        
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByRegisterEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
    }
}
