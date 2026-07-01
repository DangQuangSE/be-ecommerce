package com.sport_pro_be.modules.notification.controller;

import com.sport_pro_be.common.ApiResponse;
import com.sport_pro_be.common.SecurityUtils;
import com.sport_pro_be.modules.auth.domain.User;
import com.sport_pro_be.modules.notification.dto.NotificationDto;
import com.sport_pro_be.modules.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "Notification management APIs")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Get admin notifications")
    public ResponseEntity<ApiResponse<Page<NotificationDto>>> getAdminNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(ApiResponse.of(
                "Fetched admin notifications successfully",
                notificationService.getAdminNotifications(page, size)
        ));
    }

    @PutMapping("/admin/read-all")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Mark all admin notifications as read")
    public ResponseEntity<ApiResponse<Void>> markAllAdminNotificationsAsRead() {
        notificationService.markAllAdminNotificationsAsRead();
        return ResponseEntity.ok(ApiResponse.of("Marked all admin notifications as read", null));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get customer notifications")
    public ResponseEntity<ApiResponse<Page<NotificationDto>>> getCustomerNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        User user = SecurityUtils.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.of(
                "Fetched notifications successfully",
                notificationService.getCustomerNotifications(user.getId(), page, size)
        ));
    }

    @PutMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mark all customer notifications as read")
    public ResponseEntity<ApiResponse<Void>> markAllCustomerNotificationsAsRead() {
        User user = SecurityUtils.getCurrentUser();
        notificationService.markAllCustomerNotificationsAsRead(user.getId());
        return ResponseEntity.ok(ApiResponse.of("Marked all notifications as read", null));
    }
}
