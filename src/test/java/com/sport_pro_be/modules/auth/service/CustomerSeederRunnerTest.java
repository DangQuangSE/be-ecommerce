package com.sport_pro_be.modules.auth.service;

import com.sport_pro_be.modules.auth.domain.User;
import com.sport_pro_be.modules.auth.enums.Role;
import com.sport_pro_be.modules.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/// Unit tests for {@link CustomerSeederRunner}, mirroring the seeding
/// behaviour verified for AdminSeederRunner: gating, idempotency, and the
/// fields set on freshly seeded customer accounts.
@ExtendWith(MockitoExtension.class)
class CustomerSeederRunnerTest {

    private static final String CUSTOMER_1_EMAIL = "customer1@sportpro.local";
    private static final String CUSTOMER_2_EMAIL = "customer2@sportpro.local";
    private static final String SEED_PASSWORD = "Customer@123456";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private CustomerSeederRunner runner;

    @BeforeEach
    void setUp() {
        runner = new CustomerSeederRunner(userRepository, passwordEncoder);
    }

    @Test
    void run_whenSeedDisabled_doesNothing() {
        // seedEnabled defaults to false (Java default for the boolean field) since
        // no @Value injection happens outside of a Spring context.
        runner.run();

        verify(userRepository, never()).existsByEmailIgnoreCase(anyString());
        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any(User.class));
    }

    @Test
    void run_whenEnabledButPasswordMissing_doesNothing() {
        ReflectionTestUtils.setField(runner, "seedEnabled", true);
        // customerPassword defaults to null (Java default outside a Spring context).

        runner.run();

        verify(userRepository, never()).existsByEmailIgnoreCase(anyString());
        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any(User.class));
    }

    @Test
    void run_whenEnabledAndNoExistingCustomers_seedsBothUsersWithExpectedFields() {
        ReflectionTestUtils.setField(runner, "seedEnabled", true);
        ReflectionTestUtils.setField(runner, "customerPassword", SEED_PASSWORD);
        when(userRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(passwordEncoder.encode(SEED_PASSWORD)).thenReturn("ENCODED_HASH");

        runner.run();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(2)).save(captor.capture());
        List<User> savedUsers = captor.getAllValues();

        User customer1 = savedUsers.stream()
                .filter(u -> CUSTOMER_1_EMAIL.equals(u.getEmail()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("customer1 was not seeded"));
        User customer2 = savedUsers.stream()
                .filter(u -> CUSTOMER_2_EMAIL.equals(u.getEmail()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("customer2 was not seeded"));

        for (User user : List.of(customer1, customer2)) {
            assertEquals(Role.USER, user.getRole());
            assertEquals("ENCODED_HASH", user.getPasswordHash());
            assertTrue(user.isEmailVerified());
            assertTrue(user.isActive());
        }
        assertEquals("Nguyễn", customer1.getFirstName());
        assertEquals("Văn A", customer1.getLastName());
        assertEquals("Trần", customer2.getFirstName());
        assertEquals("Thị B", customer2.getLastName());

        verify(passwordEncoder, times(2)).encode(SEED_PASSWORD);
    }

    @Test
    void run_whenACustomerAlreadyExists_skipsOnlyThatCustomer() {
        ReflectionTestUtils.setField(runner, "seedEnabled", true);
        ReflectionTestUtils.setField(runner, "customerPassword", SEED_PASSWORD);
        when(userRepository.existsByEmailIgnoreCase(eq(CUSTOMER_1_EMAIL))).thenReturn(true);
        when(userRepository.existsByEmailIgnoreCase(eq(CUSTOMER_2_EMAIL))).thenReturn(false);
        when(passwordEncoder.encode(SEED_PASSWORD)).thenReturn("ENCODED_HASH");

        runner.run();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(captor.capture());
        assertEquals(CUSTOMER_2_EMAIL, captor.getValue().getEmail());
    }

    @Test
    void run_whenBothCustomersAlreadyExist_savesNoOne() {
        ReflectionTestUtils.setField(runner, "seedEnabled", true);
        ReflectionTestUtils.setField(runner, "customerPassword", SEED_PASSWORD);
        when(userRepository.existsByEmailIgnoreCase(anyString())).thenReturn(true);

        runner.run();

        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any(User.class));
    }
}
