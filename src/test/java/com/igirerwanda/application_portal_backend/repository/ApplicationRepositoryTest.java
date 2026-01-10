package com.igirerwanda.application_portal_backend.repository;

import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.application.repository.ApplicationRepository;
import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import com.igirerwanda.application_portal_backend.user.entity.User;
import com.igirerwanda.application_portal_backend.auth.repository.RegisterRepository;
import com.igirerwanda.application_portal_backend.cohort.repository.CohortRepository;
import com.igirerwanda.application_portal_backend.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ApplicationRepositoryTest {

    @Autowired private ApplicationRepository applicationRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private RegisterRepository registerRepository;
    @Autowired private CohortRepository cohortRepository;

    @Test
    void findByUserIdAndCohortId_Success() {
        // 1. Setup Dependencies
        Register register = new Register();
        register.setEmail("test@app.com");
        register.setVerified(true);
        registerRepository.save(register);

        Cohort cohort = new Cohort();
        cohort.setName("Cohort Test");
        cohortRepository.save(cohort);

        User user = new User();
        user.setRegister(register);
        user.setCohort(cohort);
        userRepository.save(user);

        // 2. Create Application
        Application app = new Application();
        app.setUser(user);
        app.setCohort(cohort);
        app.setStatus(ApplicationStatus.DRAFT);
        applicationRepository.save(app);

        // 3. Test Query
        Optional<Application> found = applicationRepository.findByUserIdAndCohortId(user.getId(), cohort.getId());
        assertTrue(found.isPresent());
    }
}