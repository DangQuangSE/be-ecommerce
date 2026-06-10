package com.sport_pro_be.modules.auth.repository;

import com.sport_pro_be.modules.auth.domain.User;
import com.sport_pro_be.modules.auth.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<User> findByRole(Role role);
}

