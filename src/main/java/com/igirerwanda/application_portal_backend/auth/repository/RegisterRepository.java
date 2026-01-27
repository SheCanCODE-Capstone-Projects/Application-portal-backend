package com.igirerwanda.application_portal_backend.auth.repository;

import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.common.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface RegisterRepository extends JpaRepository<Register, UUID> {
    Optional<Register> findByEmail(String email);
    Optional<Register> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByRole(UserRole role);
}