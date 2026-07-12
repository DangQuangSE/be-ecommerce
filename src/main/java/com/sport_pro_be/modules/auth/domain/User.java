package com.sport_pro_be.modules.auth.domain;

import com.sport_pro_be.modules.auth.enums.Role;
import com.sport_pro_be.modules.auth.enums.UserTier;
import com.sport_pro_be.modules.auth.enums.AuthProvider;
import com.sport_pro_be.common.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "app_users", indexes = {
        @Index(name = "idx_user_role", columnList = "role"),
        @Index(name = "idx_user_tier", columnList = "tier")
})
public class User extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 190)
    private String email;

    private String firstName;
    private String lastName;
    private String avatar;

    @Column(nullable = true)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider authProvider = AuthProvider.LOCAL;

    @Column(nullable = false)
    private boolean emailVerified;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;

    @Column(nullable = false)
    private Integer tokenVersion = 1;

    @Column(name = "total_spending", precision = 15, scale = 2)
    private BigDecimal totalSpending = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserTier tier = UserTier.BRONZE;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean isActive = true;

    public String getFullName() {
        if (firstName == null && lastName == null) return "Unknown User";
        if (firstName == null) return lastName;
        if (lastName == null) return firstName;
        return firstName + " " + lastName;
    }
}

