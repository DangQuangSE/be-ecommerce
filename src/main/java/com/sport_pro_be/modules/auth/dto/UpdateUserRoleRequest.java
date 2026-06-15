package com.sport_pro_be.modules.auth.dto;

import com.sport_pro_be.modules.auth.enums.Role;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleRequest(
    @NotNull(message = "Role is required")
    Role role
) {}
