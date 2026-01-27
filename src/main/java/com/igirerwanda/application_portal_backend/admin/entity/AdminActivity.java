package com.igirerwanda.application_portal_backend.admin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.igirerwanda.application_portal_backend.application.entity.Application;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "admin_activity")
@Getter
@Setter
public class AdminActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "admin_id")
    @JsonIgnore
    private AdminUser admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    @JsonIgnore
    private Application application;

    @JsonProperty("adminName")
    public String getAdminName() {
        return admin != null ? admin.getName() : null;
    }

    @JsonProperty("adminEmail")
    public String getAdminEmail() {
        return admin != null ? admin.getEmail() : null;
    }

    private String action;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private String email;
}