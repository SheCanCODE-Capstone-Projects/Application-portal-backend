package com.igirerwanda.application_portal_backend.config;

import com.igirerwanda.application_portal_backend.auth.repository.UserRepository;
import com.igirerwanda.application_portal_backend.common.enums.UserRole;
import com.igirerwanda.application_portal_backend.user.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(AdminBootstrap.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Value("${app.admin.bootstrap.email:admin@igirerwanda.com}")
    private String bootstrapEmail;
    
    @Value("${app.admin.bootstrap.password:}")
    private String bootstrapPassword;
    
    @Value("${app.admin.bootstrap.enabled:true}")
    private boolean bootstrapEnabled;
    
    @Override
    public void run(String... args) throws Exception {
        if (!bootstrapEnabled) {
            logger.info("Admin bootstrap is disabled");
            return;
        }
        
        // Check if any admin users exist
        boolean adminExists = userRepository.existsByRole(UserRole.ADMIN) || 
                             userRepository.existsByRole(UserRole.SUPER_ADMIN);
        
        if (!adminExists) {
            createDefaultAdmin();
        } else {
            logger.info("Admin users already exist, skipping bootstrap");
        }
    }
    
    private void createDefaultAdmin() {
        if (bootstrapPassword == null || bootstrapPassword.trim().isEmpty()) {
            logger.warn("No bootstrap password provided. Skipping admin creation. Set app.admin.bootstrap.password");
            return;
        }
        
        User admin = new User();
        admin.setEmail(bootstrapEmail);
        admin.setFirstName("System");
        admin.setLastName("Administrator");
        admin.setPassword(passwordEncoder.encode(bootstrapPassword));
        admin.setRole(UserRole.SUPER_ADMIN);
        admin.setRequirePasswordReset(true);
        admin.setMfaEnabled(true);
        
        userRepository.save(admin);
        logger.info("Default admin user created: {}", bootstrapEmail);
    }
}