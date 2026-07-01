package com.sport_pro_be.modules.notification.repository;

import com.sport_pro_be.modules.notification.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // For Admin: userId is null
    Page<Notification> findByUserIdIsNullOrderByCreatedAtDesc(Pageable pageable);

    // For Customer: userId is specific
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // Mark all as read for Admin
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId IS NULL AND n.isRead = false")
    int markAllAdminNotificationsAsRead();

    // Mark all as read for Customer
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId AND n.isRead = false")
    int markAllCustomerNotificationsAsRead(@Param("userId") Long userId);
}
