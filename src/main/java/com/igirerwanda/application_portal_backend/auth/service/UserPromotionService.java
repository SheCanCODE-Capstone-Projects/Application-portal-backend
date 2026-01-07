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

    public UserPromotionService(UserRepository userRepo) {
        this.userRepo = userRepo;

    }

    @Transactional
    public User promote(Register register) {

        if (register.getUser() != null) {
            return register.getUser();
        }

        User user = new User();
        user.setRegister(register);
        user.setStatus(UserStatus.ACTIVE);
        return userRepo.save(user);
    }
}