package com.igirerwanda.application_portal_backend.seed;

import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.auth.repository.RegisterRepository;
import com.igirerwanda.application_portal_backend.common.enums.AuthProvider;
import com.igirerwanda.application_portal_backend.common.enums.UserRole;
import com.igirerwanda.application_portal_backend.common.enums.UserStatus;
import com.igirerwanda.application_portal_backend.user.entity.User;
import com.igirerwanda.application_portal_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final RegisterRepository registerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {

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
        user.setStatus(UserStatus.ACTIVE);

        userRepository.save(user);

        System.out.println("✅ Admin user created successfully");
    }
}