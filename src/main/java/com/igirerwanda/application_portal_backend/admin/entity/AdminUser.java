package com.igirerwanda.application_portal_backend.admin.entity;

import com.igirerwanda.application_portal_backend.common.enums.AdminRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "admin_users")
@Getter
@Setter
public class AdminUser {

    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private AdminRole role;

    private String password;
}

