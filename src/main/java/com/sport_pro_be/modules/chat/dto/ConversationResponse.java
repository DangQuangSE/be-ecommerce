package com.sport_pro_be.modules.chat.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ConversationResponse {
    private Long id;

    /// Display name of the other party (shop title for the customer view,
    /// customer name for the admin view).
    private String name;
    private String avatar;

    private String lastMessage;
    @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private LocalDateTime lastMessageAt;

    /// Unread count for the requesting viewer.
    private int unreadCount;

    private String tag;
    private boolean online;
    private String associatedProductImage;
}
