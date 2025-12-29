package com.igirerwanda.application_portal_backend.user.repository;

import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByRegister(Register register);

    Optional<User> findByRegister(Register register);
    
    @Query("SELECT u FROM User u WHERE u.register.email = :email")
    Optional<User> findByRegisterEmail(@Param("email") String email);
    
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.register.email = :email")
    boolean existsByRegisterEmail(@Param("email") String email);
}
