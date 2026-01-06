package com.igirerwanda.application_portal_backend.notification.entity;

import com.igirerwanda.application_portal_backend.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private boolean isRead = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime readAt;

    // Application-related fields
    private Long applicationId;
    private String applicationStatus;

    public enum NotificationType {
        APPLICATION_SUBMITTED,
        APPLICATION_UNDER_REVIEW,
        APPLICATION_ACCEPTED,
        APPLICATION_REJECTED,
        INTERVIEW_SCHEDULED,
        GENERAL
    }
}