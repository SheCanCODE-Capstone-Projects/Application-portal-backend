package com.igirerwanda.application_portal_backend.user.entity;

import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import com.igirerwanda.application_portal_backend.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "register_id", nullable = false)
    private Register register;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cohort_id")
    private Cohort cohort;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.PENDING_VERIFICATION;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void softDelete() {
        this.status = UserStatus.DISABLED;
    }

    public void archive() {
        this.status = UserStatus.ARCHIVED;
    }
}