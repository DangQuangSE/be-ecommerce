package com.sport_pro_be.modules.chat.service;

import com.sport_pro_be.modules.chat.dto.ConversationResponse;
import com.sport_pro_be.modules.chat.dto.MessageResponse;
import com.sport_pro_be.modules.chat.dto.SendMessageRequest;

import java.util.List;

public interface IChatService {

    /// Conversations visible to the current user: their own (customer) or all
    /// (admin). A customer with none gets a default support conversation.
    List<ConversationResponse> getMyConversations();

    /// Messages in a conversation (chronological). Marks the viewer's side read.
    List<MessageResponse> getMessages(Long conversationId);

    /// Posts a message as the current user (CUSTOMER or ADMIN by role).
    MessageResponse sendMessage(Long conversationId, SendMessageRequest request);
}
