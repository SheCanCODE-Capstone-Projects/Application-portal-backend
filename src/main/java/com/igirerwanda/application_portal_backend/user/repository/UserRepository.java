package com.igirerwanda.application_portal_backend.user.repository;

import com.igirerwanda.application_portal_backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByRegisterEmail(String email);
    Optional<User> findByRegisterId(UUID registerId);
}