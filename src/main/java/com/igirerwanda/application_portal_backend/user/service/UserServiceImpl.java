package com.igirerwanda.application_portal_backend.user.service;

import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import com.igirerwanda.application_portal_backend.cohort.repository.CohortRepository;
import com.igirerwanda.application_portal_backend.common.exception.NotFoundException;
import com.igirerwanda.application_portal_backend.user.dto.UserResponseDto;
import com.igirerwanda.application_portal_backend.user.entity.User;
import com.igirerwanda.application_portal_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CohortRepository cohortRepository;

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
    public User findByRegisterId(Long registerId) {
        return userRepository.findByRegisterId(registerId)
                .orElseThrow(() -> new NotFoundException("User profile not found for Account ID: " + registerId));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getCurrentUserProfile() {
        User user = getAuthenticatedUser();
        return mapToDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsersDetailed() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
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

        if (!Boolean.TRUE.equals(cohort.getIsOpen())) {
            throw new IllegalStateException("Cohort is closed");
        }

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


    private UserResponseDto mapToDto(User user) {
        UserResponseDto dto = new UserResponseDto();


        if (user.getRegister() != null) {
            dto.setEmail(user.getRegister().getEmail());
            dto.setUsername(user.getRegister().getUsername());
        }


        if (user.getCohort() != null) {
            dto.setCohortId(user.getCohort().getId());
            dto.setCohortName(user.getCohort().getName());
        }

        dto.setStatus(user.getStatus());
        dto.setCreatedAt(user.getCreatedAt());

        if (user.getCohort() != null) {
            dto.setCohortId(user.getCohort().getId());
            dto.setCohortName(user.getCohort().getName());
        }

        return dto;
    }
}