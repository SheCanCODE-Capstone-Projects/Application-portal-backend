package com.igirerwanda.application_portal_backend.me.service;

import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.application.entity.PersonalInformation;
import com.igirerwanda.application_portal_backend.me.entity.MasterUser;
import com.igirerwanda.application_portal_backend.me.repository.MasterUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Slf4j
public class MasterDataService {

    private final MasterUserRepository masterUserRepository;

    @Transactional(readOnly = true, transactionManager = "masterTransactionManager")
    public boolean isUserInMasterDatabase(PersonalInformation pi) {
        if (pi == null) return false;

        if (masterUserRepository.existsByPhoneNumber(pi.getPhone())) {
            log.warn("System Rejection: Phone {} already exists in Master DB", pi.getPhone());
            return true;
        }

        if (masterUserRepository.existsByFullNameIgnoreCase(pi.getFullName())) {
            log.warn("System Rejection: Name {} already exists in Master DB", pi.getFullName());
            return true;
        }

        return false;
    }

    @Transactional(transactionManager = "masterTransactionManager")
    public void syncUserToMaster(Application app) {
        PersonalInformation pi = app.getPersonalInformation();

        if (pi == null) return;

        if (masterUserRepository.existsByPhoneNumber(pi.getPhone())) {
            log.info("User {} already in Master DB, skipping sync.", pi.getFullName());
            return;
        }

        MasterUser master = new MasterUser();
        master.setFullName(pi.getFullName());
        master.setPhoneNumber(pi.getPhone());
        master.setOriginSystemId(app.getUser().getId().toString());
        master.setOriginSystem("IGIRERWANDA_PORTAL");
        master.setCohortJoined(app.getCohort().getName());
        master.setApplicationDate(app.getSubmittedAt());

        if (app.getUser() != null && app.getUser().getRegister() != null) {
            master.setRole(app.getUser().getRegister().getRole().name());
            master.setProvider(app.getUser().getRegister().getProvider().name());
        }

        masterUserRepository.save(master);
        log.info("Successfully synced {} to Master Data.", pi.getFullName());
    }

    @Transactional(readOnly = true, transactionManager = "masterTransactionManager")
    public long countSynchronizedUsers() {
        return masterUserRepository.count();
    }

    @Transactional(readOnly = true, transactionManager = "masterTransactionManager")
    public Page<MasterUser> getSynchronizedUsers(Pageable pageable) {
        return masterUserRepository.findAll(pageable);
    }
}