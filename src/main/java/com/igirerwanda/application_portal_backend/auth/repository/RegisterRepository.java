package com.igirerwanda.application_portal_backend.auth.repository;

import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.common.enums.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegisterRepository extends JpaRepository<Register, Long> {
    Optional<Register> findByEmail(String email);
    Optional<Register> findByUsername(String username);
    Optional<Register> findByProviderAndProviderId(AuthProvider provider, String providerId);

    boolean existsByEmail(String mail);
}

