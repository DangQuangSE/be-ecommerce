package com.sport_pro_be.modules.chat.dto;

import com.sport_pro_be.modules.chat.enums.SenderType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageResponse {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private SenderType senderType;
    private String content;
    private String imageUrl;

    /// True for SYSTEM messages (status/welcome notices).
    private boolean system;

    /// True when the requesting user authored this message (drives `isMe` on FE).
    private boolean mine;

    @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private LocalDateTime createdAt;
}
