package com.igirerwanda.application_portal_backend.repository;

import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.auth.repository.RegisterRepository;
import com.igirerwanda.application_portal_backend.user.entity.User;
import com.igirerwanda.application_portal_backend.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryTest {

    @Autowired private UserRepository userRepository;
    @Autowired private RegisterRepository registerRepository;

    @Test
    void findByRegisterEmail_Success() {
        Register register = new Register();
        register.setEmail("user@repo.test");
        register.setUsername("usertest");
        registerRepository.save(register);

        User user = new User();
        user.setRegister(register);
        userRepository.save(user);

        Optional<User> found = userRepository.findByRegisterEmail("user@repo.test");
        assertTrue(found.isPresent());
    }
}