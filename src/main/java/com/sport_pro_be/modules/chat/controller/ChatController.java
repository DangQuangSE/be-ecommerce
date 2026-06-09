package com.sport_pro_be.modules.chat.controller;

import com.sport_pro_be.common.ApiResponse;
import com.sport_pro_be.modules.chat.constant.ChatConstant;
import com.sport_pro_be.modules.chat.dto.ConversationResponse;
import com.sport_pro_be.modules.chat.dto.MessageResponse;
import com.sport_pro_be.modules.chat.dto.SendMessageRequest;
import com.sport_pro_be.modules.chat.service.IChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/// Chat for any authenticated user. A USER sees their own conversations; an
/// ADMIN sees all (the support inbox). Requires authentication via the global
/// security rule (`anyRequest().authenticated()`).
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final IChatService chatService;

    @GetMapping("/conversations")
    public ResponseEntity<ApiResponse<List<ConversationResponse>>> getMyConversations() {
        return ResponseEntity.ok(ApiResponse.of(
                ChatConstant.CONVERSATIONS_RETRIEVED, chatService.getMyConversations()));
    }

    @GetMapping("/conversations/{id}/messages")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getMessages(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of(
                ChatConstant.MESSAGES_RETRIEVED, chatService.getMessages(id)));
    }

    @PostMapping("/conversations/{id}/messages")
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @PathVariable Long id,
            @Valid @RequestBody SendMessageRequest request) {
        return ResponseEntity.ok(ApiResponse.of(
                ChatConstant.MESSAGE_SENT, chatService.sendMessage(id, request)));
    }
}
