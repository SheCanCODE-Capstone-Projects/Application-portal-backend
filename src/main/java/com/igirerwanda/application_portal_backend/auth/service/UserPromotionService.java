package com.igirerwanda.application_portal_backend.auth.service;

import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.common.enums.UserStatus;
import com.igirerwanda.application_portal_backend.notification.service.NotificationService;
import com.igirerwanda.application_portal_backend.user.entity.User;
import com.igirerwanda.application_portal_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserPromotionService {

    private final UserRepository userRepo;
    private final NotificationService notificationService;

    @Transactional
    public User promote(Register register) {
        if (register.getUser() != null) {
            return register.getUser();
        }

        User user = new User();
        user.setRegister(register);
        user.setStatus(UserStatus.ACTIVE);
        user = userRepo.save(user);

        register.setUser(user);

        notificationService.sendAccountActivatedNotification(user);

        return user;
    }
}