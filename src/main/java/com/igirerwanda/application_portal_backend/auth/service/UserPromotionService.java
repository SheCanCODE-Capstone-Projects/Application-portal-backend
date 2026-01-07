package com.igirerwanda.application_portal_backend.auth.service;

import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.application.repository.ApplicationRepository;
import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import com.igirerwanda.application_portal_backend.cohort.repository.CohortRepository;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import com.igirerwanda.application_portal_backend.common.enums.UserStatus;
import com.igirerwanda.application_portal_backend.user.entity.User;
import com.igirerwanda.application_portal_backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import this

@Service
public class UserPromotionService {

    private final UserRepository userRepo;
    private final CohortRepository cohortRepo;
    private final ApplicationRepository applicationRepo;

    public UserPromotionService(UserRepository userRepo,
                                CohortRepository cohortRepo,
                                ApplicationRepository applicationRepo) {
        this.userRepo = userRepo;
        this.cohortRepo = cohortRepo;
        this.applicationRepo = applicationRepo;
    }

    @Transactional // Added to ensure database consistency
    public User promote(Register register) {

        if (register.getUser() != null) {
            return register.getUser();
        }

        // FIX: Use .orElse(null) instead of throwing an exception
        Cohort cohort = cohortRepo.findFirstByIsOpenTrue()
                .orElse(null);

        User user = new User();
        user.setRegister(register);
        user.setCohort(cohort); // Will be null if no open cohort exists
        user.setStatus(UserStatus.ACTIVE);

        user = userRepo.save(user);

        register.setUser(user);

        Application application = new Application();
        application.setUser(user);

        // IMPORTANT: If your Application entity requires a Cohort,
        // you should set it here as well.
        if (cohort != null) {
            application.setCohort(cohort);
        }

        application.setStatus(ApplicationStatus.PENDING);
        applicationRepo.save(application);

        return user;
    }
}