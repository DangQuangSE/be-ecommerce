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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/// Unit tests for {@link CustomerSeederRunner}, mirroring the seeding
/// behaviour verified for AdminSeederRunner: gating, idempotency, and the
/// fields set on the freshly seeded customer account.
@ExtendWith(MockitoExtension.class)
class CustomerSeederRunnerTest {

    private static final String CUSTOMER_EMAIL = "customer@sportpro.local";
    private static final String CUSTOMER_PASSWORD = "Customer@123456";

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
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void run_whenEnabledButEmailOrPasswordMissing_doesNothing() {
        ReflectionTestUtils.setField(runner, "seedEnabled", true);
        // customerEmail/customerPassword default to null outside a Spring context.

        runner.run();

        verify(userRepository, never()).existsByEmailIgnoreCase(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void run_whenEnabledAndCustomerDoesNotExist_seedsUserWithExpectedFields() {
        ReflectionTestUtils.setField(runner, "seedEnabled", true);
        ReflectionTestUtils.setField(runner, "customerEmail", CUSTOMER_EMAIL);
        ReflectionTestUtils.setField(runner, "customerPassword", CUSTOMER_PASSWORD);
        when(userRepository.existsByEmailIgnoreCase(CUSTOMER_EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(CUSTOMER_PASSWORD)).thenReturn("ENCODED_HASH");

        runner.run();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User customer = captor.getValue();

        assertEquals(CUSTOMER_EMAIL, customer.getEmail());
        assertEquals(Role.USER, customer.getRole());
        assertEquals("ENCODED_HASH", customer.getPasswordHash());
        assertTrue(customer.isEmailVerified());
        assertTrue(customer.isActive());
    }

    @Test
    void run_whenCustomerAlreadyExists_skipsSeed() {
        ReflectionTestUtils.setField(runner, "seedEnabled", true);
        ReflectionTestUtils.setField(runner, "customerEmail", CUSTOMER_EMAIL);
        ReflectionTestUtils.setField(runner, "customerPassword", CUSTOMER_PASSWORD);
        when(userRepository.existsByEmailIgnoreCase(eq(CUSTOMER_EMAIL))).thenReturn(true);

        runner.run();

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void run_whenEmailHasWhitespaceAndMixedCase_normalizesBeforeLookupAndSave() {
        ReflectionTestUtils.setField(runner, "seedEnabled", true);
        ReflectionTestUtils.setField(runner, "customerEmail", "  Customer@SportPro.LOCAL  ");
        ReflectionTestUtils.setField(runner, "customerPassword", CUSTOMER_PASSWORD);
        when(userRepository.existsByEmailIgnoreCase(CUSTOMER_EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(CUSTOMER_PASSWORD)).thenReturn("ENCODED_HASH");

        runner.run();

        verify(userRepository).existsByEmailIgnoreCase(CUSTOMER_EMAIL);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(CUSTOMER_EMAIL, captor.getValue().getEmail());
    }
}
