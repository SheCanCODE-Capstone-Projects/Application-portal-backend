package com.igirerwanda.application_portal_backend.admin.repository;

import com.igirerwanda.application_portal_backend.admin.entity.AdminActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AdminActivityRepository extends JpaRepository<AdminActivity, UUID> {
    List<AdminActivity> findByAdmin_EmailOrderByCreatedAtDesc(String email);
    List<AdminActivity> findAllByOrderByCreatedAtDesc();
}