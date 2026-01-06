package com.igirerwanda.application_portal_backend.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.igirerwanda.application_portal_backend.user.entity.User;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne
    private Register register;

    private LocalDateTime expiryDate;
}
