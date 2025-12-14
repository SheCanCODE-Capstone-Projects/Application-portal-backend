package com.igirerwanda.application_portal_backend.auth.repository;

import com.igirerwanda.application_portal_backend.auth.entity.EmailVerificationToken;
import com.igirerwanda.application_portal_backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    
    Optional<EmailVerificationToken> findByUser(User user);
    
    @Modifying
    @Query("DELETE FROM EmailVerificationToken e WHERE e.user = :user")
    void deleteByUser(User user);
}