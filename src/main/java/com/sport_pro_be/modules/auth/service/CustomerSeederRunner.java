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
public class CustomerSeederRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${APP_CUSTOMER_SEED_ENABLED:false}")
    private boolean seedEnabled;

    @Value("${APP_CUSTOMER_EMAIL:}")
    private String customerEmail;

    @Value("${APP_CUSTOMER_PASSWORD:}")
    private String customerPassword;

    @Override
    @Transactional
    public void run(String... args) {
        if (!seedEnabled) {
            log.info("Customer seed disabled (APP_CUSTOMER_SEED_ENABLED is not true). Skipping.");
            return;
        }

        if (customerEmail == null || customerEmail.isBlank() || customerPassword == null || customerPassword.isBlank()) {
            log.warn("Customer seed enabled but APP_CUSTOMER_EMAIL or APP_CUSTOMER_PASSWORD is missing. Skipping.");
            return;
        }

        String normalizedEmail = normalizeEmail(customerEmail);
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            log.info("Customer user already exists for email {}. Skipping seed.", normalizedEmail);
            return;
        }

        User customer = new User();
        customer.setEmail(normalizedEmail);
        customer.setPasswordHash(passwordEncoder.encode(customerPassword));
        customer.setRole(Role.USER);
        customer.setEmailVerified(true);
        customer.setActive(true);
        userRepository.save(customer);

        log.info("Successfully seeded customer user for email {}.", normalizedEmail);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
