package com.sport_pro_be.modules.auth.controller;

import com.sport_pro_be.common.ApiResponse;
import com.sport_pro_be.modules.auth.dto.UpdateUserActiveRequest;
import com.sport_pro_be.modules.auth.dto.UpdateUserRoleRequest;
import com.sport_pro_be.modules.auth.dto.UserProfileResponse;
import com.sport_pro_be.modules.auth.interfaces.IProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final IProfileService profileService;

    @GetMapping
    public ApiResponse<List<UserProfileResponse>> getAllUsers() {
        return ApiResponse.of("Users retrieved successfully", profileService.getAllProfiles());
    }

    @PutMapping("/{id}/role")
    public ApiResponse<UserProfileResponse> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRoleRequest request) {
        return ApiResponse.of("User role updated successfully",
                profileService.updateUserRole(id, request.role()));
    }

    @PutMapping("/{id}/active")
    public ApiResponse<UserProfileResponse> setUserActive(
            @PathVariable Long id,
            @RequestBody UpdateUserActiveRequest request) {
        return ApiResponse.of("User active status updated successfully",
                profileService.setUserActive(id, request.active()));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        profileService.deleteUser(id);
        return ApiResponse.of("User deleted successfully", null);
    }
}
