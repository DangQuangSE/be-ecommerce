package com.sport_pro_be.modules.notification.service;

import com.sport_pro_be.modules.notification.domain.Notification;
import com.sport_pro_be.modules.notification.dto.NotificationDto;
import com.sport_pro_be.modules.notification.enums.NotificationType;
import com.sport_pro_be.modules.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void createAdminNotification(String title, String message, NotificationType type, Long relatedId, String customerName) {
        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .type(type)
                .relatedId(relatedId)
                .userId(null) // Admin
                .isRead(false)
                .build();
        
        notification = notificationRepository.save(notification);

        NotificationDto dto = mapToDto(notification, customerName);
        
        // Push to socket
        try {
            messagingTemplate.convertAndSend("/topic/admin/notifications", dto);
        } catch (Exception e) {
            log.error("Failed to send admin notification to socket", e);
        }
    }

    @Transactional
    public void createCustomerNotification(Long userId, String title, String message, NotificationType type, Long relatedId) {
        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .type(type)
                .relatedId(relatedId)
                .userId(userId)
                .isRead(false)
                .build();

        notification = notificationRepository.save(notification);

        NotificationDto dto = mapToDto(notification, null);

        // Push to socket
        try {
            messagingTemplate.convertAndSend("/topic/user/" + userId + "/notifications", dto);
        } catch (Exception e) {
            log.error("Failed to send customer notification to socket", e);
        }
    }

    @Transactional(readOnly = true)
    public Page<NotificationDto> getAdminNotifications(int page, int size) {
        return notificationRepository.findByUserIdIsNullOrderByCreatedAtDesc(PageRequest.of(page, size))
                .map(n -> mapToDto(n, null)); // Optionally we can enrich customerName if needed, but it's okay for now
    }

    @Transactional(readOnly = true)
    public Page<NotificationDto> getCustomerNotifications(Long userId, int page, int size) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size))
                .map(n -> mapToDto(n, null));
    }

    @Transactional
    public void markAllAdminNotificationsAsRead() {
        notificationRepository.markAllAdminNotificationsAsRead();
    }

    @Transactional
    public void markAllCustomerNotificationsAsRead(Long userId) {
        notificationRepository.markAllCustomerNotificationsAsRead(userId);
    }

    private NotificationDto mapToDto(Notification notification, String customerName) {
        return NotificationDto.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .relatedId(notification.getRelatedId())
                .userId(notification.getUserId())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                // For backward compatibility:
                .orderId(notification.getRelatedId())
                .customerName(customerName)
                .build();
    }
}
