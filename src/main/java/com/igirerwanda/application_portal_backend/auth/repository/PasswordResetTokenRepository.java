package com.igirerwanda.application_portal_backend.auth.repository;

import com.igirerwanda.application_portal_backend.auth.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenHashAndUsedFalseAndExpiresAtAfter(String tokenHash, LocalDateTime now);
    void deleteByExpiresAtBefore(LocalDateTime now);
    void deleteByUserId(Long userId);
}