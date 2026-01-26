package com.igirerwanda.application_portal_backend.seed;

import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.auth.repository.RegisterRepository;
import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import com.igirerwanda.application_portal_backend.cohort.repository.CohortRepository;
import com.igirerwanda.application_portal_backend.common.enums.AuthProvider;
import com.igirerwanda.application_portal_backend.common.enums.UserRole;
import com.igirerwanda.application_portal_backend.common.enums.UserStatus;
import com.igirerwanda.application_portal_backend.common.enums.Gender;
import com.igirerwanda.application_portal_backend.common.enums.EducationalLevel;
import com.igirerwanda.application_portal_backend.user.entity.User;
import com.igirerwanda.application_portal_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final RegisterRepository registerRepository;
    private final UserRepository userRepository;
    private final CohortRepository cohortRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        
        // Create default cohort if it doesn't exist
        Cohort cohort = cohortRepository.findFirstByIsOpenTrue()
                .orElseGet(() -> {
                    Cohort newCohort = new Cohort();
                    newCohort.setName("Default Cohort 2024");
                    newCohort.setDescription("Default cohort for application portal with eligibility criteria");
                    newCohort.setDomain("Technology");
                    newCohort.setYear(2024);
                    newCohort.setIsOpen(true);
                    newCohort.setApplicationLimit(100);
                    
                    // Set eligibility criteria for testing automatic system rejection
                    Set<Gender> allowedGenders = new HashSet<>();
                    allowedGenders.add(Gender.MALE);
                    allowedGenders.add(Gender.FEMALE);
                    newCohort.setAllowedGenders(allowedGenders);
                    
                    Set<String> allowedNationalities = new HashSet<>();
                    allowedNationalities.add("Rwandan");
                    allowedNationalities.add("Kenyan");
                    allowedNationalities.add("Ugandan");
                    newCohort.setAllowedNationalities(allowedNationalities);
                    
                    Set<EducationalLevel> requiredEducationLevels = new HashSet<>();
                    requiredEducationLevels.add(EducationalLevel.HIGH_SCHOOL);
                    requiredEducationLevels.add(EducationalLevel.DIPLOMA);
                    requiredEducationLevels.add(EducationalLevel.BACHELOR);
                    requiredEducationLevels.add(EducationalLevel.MASTER);
                    requiredEducationLevels.add(EducationalLevel.PHD);
                    newCohort.setRequiredEducationLevels(requiredEducationLevels);
                    
                    return cohortRepository.save(newCohort);
                });

        if (registerRepository.existsByRole(UserRole.ADMIN)) {
            return;
        }

        Register register = new Register();
        register.setEmail("admin@portal.com");
        register.setUsername("admin");
        register.setPassword(passwordEncoder.encode("admin123"));
        register.setRole(UserRole.ADMIN);
        register.setVerified(true);
        register.setProvider(AuthProvider.LOCAL);

        Register savedRegister = registerRepository.save(register);

        User user = new User();
        user.setRegister(savedRegister);
        user.setCohort(cohort);
        user.setStatus(UserStatus.ACTIVE);

        userRepository.save(user);

        System.out.println("✅ Admin user and default cohort created successfully");
    }
}