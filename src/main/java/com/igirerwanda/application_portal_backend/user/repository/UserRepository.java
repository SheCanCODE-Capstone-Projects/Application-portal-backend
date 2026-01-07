package com.igirerwanda.application_portal_backend.user.repository;

import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByRegister(Register register);

    Optional<User> findByRegister(Register register);

    // FIX: This allows looking up User via Register's email field
    Optional<User> findByRegisterEmail(String email);
}