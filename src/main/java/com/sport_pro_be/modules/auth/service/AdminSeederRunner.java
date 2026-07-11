package com.sport_pro_be.modules.auth.service;

import com.sport_pro_be.modules.auth.domain.User;
import com.sport_pro_be.modules.auth.enums.Role;
import com.sport_pro_be.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Component
@Slf4j
@RequiredArgsConstructor
public class AdminSeederRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${APP_ADMIN_SEED_ENABLED:false}")
    private boolean seedEnabled;

    @Value("${APP_ADMIN_EMAIL:}")
    private String adminEmail;

    @Value("${APP_ADMIN_PASSWORD:}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(String... args) {
        if (!seedEnabled) {
            log.info("Admin seed disabled (APP_ADMIN_SEED_ENABLED is not true). Skipping.");
            return;
        }

        if (adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()) {
            log.warn("Admin seed enabled but APP_ADMIN_EMAIL or APP_ADMIN_PASSWORD is missing. Skipping.");
            return;
        }

        String normalizedEmail = normalizeEmail(adminEmail);
        if (!userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            User admin = new User();
            admin.setEmail(normalizedEmail);
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            admin.setEmailVerified(true);
            admin.setActive(true);
            userRepository.save(admin);
            log.info("Successfully seeded admin user for email {}.", normalizedEmail);
        }

        // Seed test user
        String testEmail = "test@sportpro.com";
        if (!userRepository.existsByEmailIgnoreCase(testEmail)) {
            User testUser = new User();
            testUser.setEmail(testEmail);
            testUser.setPasswordHash(passwordEncoder.encode("Test@123456"));
            testUser.setRole(Role.USER);
            testUser.setEmailVerified(true);
            testUser.setActive(true);
            userRepository.save(testUser);
            log.info("Successfully seeded test user for email {}.", testEmail);
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
