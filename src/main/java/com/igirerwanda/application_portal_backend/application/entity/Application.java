package com.igirerwanda.application_portal_backend.application.entity;

import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import com.igirerwanda.application_portal_backend.user.entity.User;
import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "applications",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "cohort_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Cohort is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cohort_id", nullable = false)
    private Cohort cohort;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.DRAFT;


    private boolean isSystemRejected = false;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(nullable = false)
    private boolean archived = false;


    private LocalDateTime submittedAt;

    private LocalDateTime interviewDate;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String systemRejectionReason;

    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL)
    private PersonalInformation personalInformation;
}