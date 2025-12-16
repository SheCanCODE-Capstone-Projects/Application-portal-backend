package com.igirerwanda.application_portal_backend.user.repository;

import com.igirerwanda.application_portal_backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    List<User> findByStatus(String status);
    
    @Query("SELECT u FROM User u WHERE u.register.email = :email")
    Optional<User> findByEmail(@Param("email") String email);
    
    List<User> findByStatusNot(String status);
}