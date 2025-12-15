package com.igirerwanda.application_portal_backend.auth.repository;

import com.igirerwanda.application_portal_backend.auth.entity.PasswordResetToken;
import com.igirerwanda.application_portal_backend.auth.entity.Register;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    long countByRegister(Register user);

    Optional<PasswordResetToken> findByRegister(Register user);
}
