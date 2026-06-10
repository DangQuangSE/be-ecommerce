package com.sport_pro_be.modules.chat.domain;

import com.sport_pro_be.common.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/// A 1-1 support conversation between a customer and the shop (admin staff).
/// Customer name/avatar are denormalised here so the admin inbox can render
/// without joining `app_users`.
@Entity
@Table(name = "chat_conversations", indexes = {
        @Index(name = "idx_conversation_customer", columnList = "customer_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversation extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "customer_name", length = 150)
    private String customerName;

    @Column(name = "customer_avatar")
    private String customerAvatar;

    /// Display title shown to the customer (e.g. "Hỗ trợ Sport Pro").
    @Column(length = 150)
    private String title;

    /// Free-form tag: "Hỗ trợ", "Đơn hàng", "Shop"...
    @Column(length = 40)
    private String tag;

    @Column(name = "last_message", columnDefinition = "TEXT")
    private String lastMessage;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @Column(name = "customer_unread", nullable = false)
    private int customerUnread;

    @Column(name = "admin_unread", nullable = false)
    private int adminUnread;
}
