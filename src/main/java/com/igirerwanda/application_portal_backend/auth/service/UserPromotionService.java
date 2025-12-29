package com.igirerwanda.application_portal_backend.auth.service;

import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import com.igirerwanda.application_portal_backend.cohort.repository.CohortRepository;
import com.igirerwanda.application_portal_backend.user.entity.User;
import com.igirerwanda.application_portal_backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserPromotionService {

    private final UserRepository userRepo;
    private final CohortRepository cohortRepo;

    public UserPromotionService(UserRepository userRepo, CohortRepository cohortRepo) {
        this.userRepo = userRepo;
        this.cohortRepo = cohortRepo;
    }

    public User promote(Register register) {
        if (register.getUser() != null) {
            return register.getUser();
        }

        Cohort cohort = (Cohort) cohortRepo.findFirstByIsOpenTrue()
                .orElseThrow(() -> new RuntimeException("No open cohort available"));

        User user = new User();
        user.setRegister(register);
        user.setCohort(cohort);

        userRepo.save(user);

        register.setUser(user); // link back

        return user;
    }
}

