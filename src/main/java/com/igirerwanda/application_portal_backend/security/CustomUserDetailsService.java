package com.igirerwanda.application_portal_backend.security;

import com.igirerwanda.application_portal_backend.auth.entity.Register;
import com.igirerwanda.application_portal_backend.auth.repository.RegisterRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final RegisterRepository registerRepository;

    public CustomUserDetailsService(RegisterRepository registerRepository) {
        this.registerRepository = registerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Register user = registerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isVerified(), // enabled only if email verified
                true,
                true,
                true,
                java.util.Collections.singletonList(
                        new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                )
        );
    }
}
