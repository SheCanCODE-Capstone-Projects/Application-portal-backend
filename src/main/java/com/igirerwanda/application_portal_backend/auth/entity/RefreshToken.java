package com.igirerwanda.application_portal_backend.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Getter @Setter
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Register user;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private boolean revoked = false;
}