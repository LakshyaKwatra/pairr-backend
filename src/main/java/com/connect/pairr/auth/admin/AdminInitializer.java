package com.connect.pairr.auth.admin;

import com.connect.pairr.model.entity.User;
import com.connect.pairr.model.enums.Role;
import com.connect.pairr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void createAdmin() {

        boolean adminExists = userRepository.existsByRole(Role.ADMIN);

        if (!adminExists) {

            User admin = User.builder()
                    .email("admin@pairr.com")
                    .displayName("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();

            userRepository.save(admin);
            log.info("Default admin created.");
        }
    }
}
