package com.sport_pro_be.modules.notification.dto;

import com.sport_pro_be.modules.notification.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationDto {
    private Long id;
    private String title;
    private String message;
    private NotificationType type;
    private Long relatedId;
    private Long userId; // null for Admin
    private boolean isRead;
    @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private LocalDateTime createdAt;
    
    // Additional fields for backward compatibility with AdminNotificationMessage
    private Long orderId; // same as relatedId
    private String customerName; // We might need this for admin
}
