package com.igirerwanda.application_portal_backend.auth.service;

import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.common.enums.AuthProvider;
import com.igirerwanda.application_portal_backend.user.entity.User;
import com.igirerwanda.application_portal_backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPromotionService {

    private final UserRepository userRepository;

    @Transactional
    public void promote(Register register) {

        // already promoted
        userRepository.findByRegister(register)
                .orElseGet(() -> {

                    if (!register.isVerified()
                            && register.getProvider() != AuthProvider.GOOGLE) {
                        throw new IllegalStateException("Register not verified");
                    }

                    User user = new User();
                    user.setRegister(register);

                    return userRepository.save(user);
                });
    }
}
