package com.igirerwanda.application_portal_backend.admin.repository;

import com.igirerwanda.application_portal_backend.admin.entity.AdminNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AdminNotificationRepository extends JpaRepository<AdminNotification, UUID> {

    List<AdminNotification> findByAdminEmailOrderByCreatedAtDesc(String adminEmail);

    List<AdminNotification> findByAdminEmailAndIsReadFalseOrderByCreatedAtDesc(String adminEmail);

    long countByAdminEmailAndIsReadFalse(String adminEmail);

    // Broadcast notifications (sent to all admins) use adminEmail = "ALL"
    List<AdminNotification> findByAdminEmailInOrderByCreatedAtDesc(List<String> emails);
}