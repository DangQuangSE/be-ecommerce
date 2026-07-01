package com.sport_pro_be.modules.notification.domain;

import com.sport_pro_be.common.AbstractAuditingEntity;
import com.sport_pro_be.modules.notification.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notification_user_id", columnList = "user_id"),
        @Index(name = "idx_notification_is_read", columnList = "is_read")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    @Column(name = "related_id")
    private Long relatedId; // e.g., orderId

    @Column(name = "user_id")
    private Long userId; // null for admin notifications, non-null for specific customer

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private boolean isRead = false;
}
