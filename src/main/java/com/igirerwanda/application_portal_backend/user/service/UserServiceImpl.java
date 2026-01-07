package com.igirerwanda.application_portal_backend.user.service;

import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import com.igirerwanda.application_portal_backend.cohort.repository.CohortRepository;
import com.igirerwanda.application_portal_backend.user.dto.UserResponseDto;
import com.igirerwanda.application_portal_backend.user.entity.User;
// import com.igirerwanda.application_portal_backend.user.mapper.UserMapper; // <--- REMOVE THIS IMPORT
import com.igirerwanda.application_portal_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CohortRepository cohortRepository;
    // private final UserMapper userMapper; // <--- REMOVE THIS FIELD

    /**
     * Helper to get the current logged-in user.
     */
    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByRegisterEmail(email)
                .orElseThrow(() -> new RuntimeException("User profile not found"));
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getCurrentUserProfile() {
        User user = getAuthenticatedUser();
        // CALL MANUAL MAPPING METHOD HERE
        return mapToDto(user);
    }

    @Override
    @Transactional
    public void applyToCohort(Long cohortId) {
        User user = getAuthenticatedUser();

        if (user.getCohort() != null) {
            throw new IllegalStateException("You have already applied to cohort: " + user.getCohort().getName());
        }

        Cohort cohort = cohortRepository.findById(cohortId)
                .orElseThrow(() -> new RuntimeException("Cohort not found"));

        user.setCohort(cohort);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public User softDelete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.softDelete();
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User archive(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.archive();
        return userRepository.save(user);
    }

    // --- NEW MANUAL MAPPING METHOD ---
    private UserResponseDto mapToDto(User user) {
        UserResponseDto dto = new UserResponseDto();

        // Map Register details (email/username)
        if (user.getRegister() != null) {
            dto.setEmail(user.getRegister().getEmail());
            dto.setUsername(user.getRegister().getUsername());
        }

        // Map Cohort details
        if (user.getCohort() != null) {
            dto.setCohortId(user.getCohort().getId());
            dto.setCohortName(user.getCohort().getName());
        }

        // Map any other fields you need here
        // dto.setId(user.getId());

        return dto;
    }
}