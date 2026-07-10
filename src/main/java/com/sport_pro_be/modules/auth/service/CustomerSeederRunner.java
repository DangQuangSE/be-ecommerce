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

import java.util.List;
import java.util.Locale;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerSeederRunner implements CommandLineRunner {

    private static final List<SeedCustomer> SEED_CUSTOMERS = List.of(
            new SeedCustomer("customer1@sportpro.local", "Nguyễn", "Văn A"),
            new SeedCustomer("customer2@sportpro.local", "Trần", "Thị B")
    );

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${APP_CUSTOMER_SEED_ENABLED:false}")
    private boolean seedEnabled;

    @Value("${APP_CUSTOMER_PASSWORD:}")
    private String customerPassword;

    @Override
    @Transactional
    public void run(String... args) {
        if (!seedEnabled) {
            log.info("Customer seed disabled (APP_CUSTOMER_SEED_ENABLED is not true). Skipping.");
            return;
        }

        if (customerPassword == null || customerPassword.isBlank()) {
            log.warn("Customer seed enabled but APP_CUSTOMER_PASSWORD is missing. Skipping.");
            return;
        }

        for (SeedCustomer seedCustomer : SEED_CUSTOMERS) {
            String normalizedEmail = normalizeEmail(seedCustomer.email());
            if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
                log.info("Customer user already exists for email {}. Skipping seed.", normalizedEmail);
                continue;
            }

            User customer = new User();
            customer.setEmail(normalizedEmail);
            customer.setPasswordHash(passwordEncoder.encode(customerPassword));
            customer.setFirstName(seedCustomer.firstName());
            customer.setLastName(seedCustomer.lastName());
            customer.setRole(Role.USER);
            customer.setEmailVerified(true);
            customer.setActive(true);
            userRepository.save(customer);

            log.info("Successfully seeded customer user for email {}.", normalizedEmail);
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private record SeedCustomer(String email, String firstName, String lastName) {}
}
