package com.igirerwanda.application_portal_backend.seed;

import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.auth.repository.RegisterRepository;
import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import com.igirerwanda.application_portal_backend.cohort.repository.CohortRepository;
import com.igirerwanda.application_portal_backend.common.enums.AuthProvider;
import com.igirerwanda.application_portal_backend.common.enums.UserRole;
import com.igirerwanda.application_portal_backend.common.enums.UserStatus;
import com.igirerwanda.application_portal_backend.user.entity.User;
import com.igirerwanda.application_portal_backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

//@Component
public class AdminSeeder implements CommandLineRunner {

    @Autowired
    private RegisterRepository registerRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CohortRepository cohortRepository;


    @Override
    public void run(String... args) {
        if (!registerRepository.existsByEmail("admin@portal.com")) {
            Register register = new Register();
            register.setEmail("admin@portal.com");
            register.setUsername("admin");
            register.setPassword(passwordEncoder.encode("admin123"));
            register.setRole(UserRole.ADMIN);
            register.setVerified(true);
            register.setProvider(AuthProvider.valueOf("LOCAL"));
            
            Register savedRegister = registerRepository.save(register);

            Cohort cohort = cohortRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("Default cohort not found"));
            
            User user = new User();
            user.setRegister(savedRegister);
            user.setCohort(cohort);
            user.setStatus("ACTIVE");
            
            userRepository.save(user);
        }
    }
}
