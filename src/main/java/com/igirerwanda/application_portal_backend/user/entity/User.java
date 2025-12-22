package com.igirerwanda.application_portal_backend.user.entity;
import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "register_id", nullable = false)
    private Register register;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cohort_id", nullable = false)
    private Cohort cohort;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void setEmail(@Email String email) {
        this.register.setEmail(email);
    }

    public String getEmail() {
        return this.register.getEmail();
    }
    
    public void setUsername(String username) {
        this.register.setUsername(username);
    }
    
    public String getUsername() {
        return this.register.getUsername();
    }
    
    public void setStatus(String status) {
        this.status = UserStatus.valueOf(status);
    }
    
    public String getStatus() {
        return this.status.toString();
    }
}

