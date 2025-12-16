package com.igirerwanda.application_portal_backend.admin.entity;

import com.igirerwanda.application_portal_backend.application.entity.Application;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "admin_activity")
@Getter
@Setter
public class AdminActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private AdminUser admin;

    @ManyToOne
    private Application application;

    private String action;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private String email; // Add this to store the actual email
}
