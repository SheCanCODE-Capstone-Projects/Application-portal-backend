package com.igirerwanda.application_portal_backend.me.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "master_beneficiaries", indexes = {
        @Index(name = "idx_master_phone", columnList = "phoneNumber"),
        @Index(name = "idx_master_nid", columnList = "origin_id")
})
@Data
public class MasterUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    @Column(name = "origin_id")
    private String originSystemId;

    @Column(name = "origin_system")
    private String originSystem;

    @Column(name = "cohort_joined")
    private String cohortJoined;

    @Column(name = "application_date")
    private LocalDateTime applicationDate;

    // --- NEW FIELDS ---
    @Column(name = "role")
    private String role;

    @Column(name = "provider")
    private String provider;

    private LocalDateTime syncedAt;

    @PrePersist
    protected void onCreate() {
        this.syncedAt = LocalDateTime.now();
    }
}