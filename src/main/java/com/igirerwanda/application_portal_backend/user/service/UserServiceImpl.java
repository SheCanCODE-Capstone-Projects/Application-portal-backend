package com.igirerwanda.application_portal_backend.user.service;

import com.igirerwanda.application_portal_backend.common.exception.NotFoundException;
import com.igirerwanda.application_portal_backend.common.exception.DuplicateResourceException;
import com.igirerwanda.application_portal_backend.user.entity.User;
import com.igirerwanda.application_portal_backend.user.dto.UserDto;
import com.igirerwanda.application_portal_backend.user.repository.UserRepository;
import com.igirerwanda.application_portal_backend.auth.repository.RegisterRepository;
import com.igirerwanda.application_portal_backend.auth.entity.Register;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RegisterRepository registerRepository;

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    @Override
    public User createUser(UserDto userDto) {
        // Check if user already exists
        if (userRepository.existsById(userDto.getId())) {
            throw new DuplicateResourceException("User already exists with id: " + userDto.getId());
        }

        // Find the register record
        Register register = registerRepository.findById(userDto.getRegisterId())
                .orElseThrow(() -> new NotFoundException("Register record not found"));

        // Create user
        User user = new User();
        user.setRegister(register);
        user.setStatus(userDto.getStatus());
        
        user = userRepository.save(user);
        log.info("User created successfully with id: {}", user.getId());
        
        return user;
    }

    @Override
    public User updateUser(Long id, UserDto userDto) {
        User user = findById(id);
        
        if (userDto.getStatus() != null) {
            user.setStatus(userDto.getStatus());
        }
        
        user = userRepository.save(user);
        log.info("User updated successfully with id: {}", user.getId());
        
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found with id: " + id);
        }
        
        userRepository.deleteById(id);
        log.info("User deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }
}
