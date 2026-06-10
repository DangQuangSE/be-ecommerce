package com.sport_pro_be.modules.chat.service;

import com.sport_pro_be.common.SecurityUtils;
import com.sport_pro_be.exception.ResourceNotFoundException;
import com.sport_pro_be.modules.auth.domain.User;
import com.sport_pro_be.modules.auth.enums.Role;
import com.sport_pro_be.modules.auth.repository.UserRepository;
import com.sport_pro_be.modules.chat.constant.ChatConstant;
import com.sport_pro_be.modules.chat.domain.Conversation;
import com.sport_pro_be.modules.chat.domain.Message;
import com.sport_pro_be.modules.chat.dto.ConversationResponse;
import com.sport_pro_be.modules.chat.dto.MessageResponse;
import com.sport_pro_be.modules.chat.dto.SendMessageRequest;
import com.sport_pro_be.modules.chat.enums.SenderType;
import com.sport_pro_be.modules.chat.repository.ConversationRepository;
import com.sport_pro_be.modules.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements IChatService {

    private static final long SYSTEM_SENDER_ID = 0L;
    private static final long PUSH_PLACEHOLDER_ID = -1L;
    private static final String USER_QUEUE = "/queue/chat";

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public List<ConversationResponse> getMyConversations() {
        User user = SecurityUtils.getCurrentUser();
        boolean adminView = user.getRole() == Role.ADMIN;

        List<Conversation> conversations = adminView
                ? conversationRepository.findAllByOrderByLastMessageAtDesc()
                : conversationRepository.findByCustomerIdOrderByLastMessageAtDesc(user.getId());

        if (!adminView && conversations.isEmpty()) {
            conversations = List.of(createDefaultConversation(user));
        }

        return conversations.stream()
                .map(c -> toConversationResponse(c, adminView))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<MessageResponse> getMessages(Long conversationId) {
        User user = SecurityUtils.getCurrentUser();
        Conversation conversation = getAuthorizedConversation(conversationId, user);

        // Opening the thread clears the viewer's unread badge.
        if (user.getRole() == Role.ADMIN) {
            conversation.setAdminUnread(0);
        } else {
            conversation.setCustomerUnread(0);
        }
        conversationRepository.save(conversation);

        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId).stream()
                .map(m -> toMessageResponse(m, user.getId()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MessageResponse sendMessage(Long conversationId, SendMessageRequest request) {
        User user = SecurityUtils.getCurrentUser();
        Conversation conversation = getAuthorizedConversation(conversationId, user);
        boolean isAdmin = user.getRole() == Role.ADMIN;

        Message message = Message.builder()
                .conversationId(conversationId)
                .senderId(user.getId())
                .senderType(isAdmin ? SenderType.ADMIN : SenderType.CUSTOMER)
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .build();
        message = messageRepository.save(message);

        conversation.setLastMessage(request.getContent());
        conversation.setLastMessageAt(message.getCreatedAt());
        if (isAdmin) {
            conversation.setCustomerUnread(conversation.getCustomerUnread() + 1);
        } else {
            conversation.setAdminUnread(conversation.getAdminUnread() + 1);
        }
        conversationRepository.save(conversation);

        broadcast(conversation, message, isAdmin);
        return toMessageResponse(message, user.getId());
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    /// Pushes a freshly-persisted message to the OTHER party's STOMP queue
    /// (`/user/{id}/queue/chat`). `mine` is forced false since the recipient is
    /// never the author. Customer messages fan out to every admin (the inbox).
    private void broadcast(Conversation conversation, Message message, boolean fromAdmin) {
        MessageResponse payload = toMessageResponse(message, PUSH_PLACEHOLDER_ID);
        if (fromAdmin) {
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(conversation.getCustomerId()), USER_QUEUE, payload);
        } else {
            for (User admin : userRepository.findByRole(Role.ADMIN)) {
                messagingTemplate.convertAndSendToUser(
                        String.valueOf(admin.getId()), USER_QUEUE, payload);
            }
        }
    }

    private Conversation getAuthorizedConversation(Long conversationId, User user) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException(ChatConstant.CONVERSATION_NOT_FOUND));
        // Customers may only touch their own conversations; admins see all.
        if (user.getRole() != Role.ADMIN && !conversation.getCustomerId().equals(user.getId())) {
            throw new ResourceNotFoundException(ChatConstant.CONVERSATION_NOT_FOUND);
        }
        return conversation;
    }

    private Conversation createDefaultConversation(User user) {
        Conversation conversation = Conversation.builder()
                .customerId(user.getId())
                .customerName(resolveName(user))
                .customerAvatar(user.getAvatar())
                .title(ChatConstant.DEFAULT_SUPPORT_TITLE)
                .tag(ChatConstant.DEFAULT_SUPPORT_TAG)
                .lastMessage(ChatConstant.WELCOME_MESSAGE)
                .lastMessageAt(LocalDateTime.now())
                .customerUnread(1)
                .adminUnread(0)
                .build();
        conversation = conversationRepository.save(conversation);

        messageRepository.save(Message.builder()
                .conversationId(conversation.getId())
                .senderId(SYSTEM_SENDER_ID)
                .senderType(SenderType.SYSTEM)
                .content(ChatConstant.WELCOME_MESSAGE)
                .build());

        return conversation;
    }

    private String resolveName(User user) {
        String first = user.getFirstName() == null ? "" : user.getFirstName();
        String last = user.getLastName() == null ? "" : user.getLastName();
        String name = (first + " " + last).trim();
        return name.isEmpty() ? user.getEmail() : name;
    }

    private ConversationResponse toConversationResponse(Conversation c, boolean adminView) {
        return ConversationResponse.builder()
                .id(c.getId())
                .name(adminView ? c.getCustomerName() : c.getTitle())
                .avatar(adminView ? c.getCustomerAvatar() : null)
                .lastMessage(c.getLastMessage())
                .lastMessageAt(c.getLastMessageAt())
                .unreadCount(adminView ? c.getAdminUnread() : c.getCustomerUnread())
                .tag(c.getTag())
                .online(false)
                .associatedProductImage(null)
                .build();
    }

    private MessageResponse toMessageResponse(Message m, Long currentUserId) {
        boolean system = m.getSenderType() == SenderType.SYSTEM;
        return MessageResponse.builder()
                .id(m.getId())
                .conversationId(m.getConversationId())
                .senderId(m.getSenderId())
                .senderType(m.getSenderType())
                .content(m.getContent())
                .imageUrl(m.getImageUrl())
                .system(system)
                .mine(!system && m.getSenderId().equals(currentUserId))
                .createdAt(m.getCreatedAt())
                .build();
    }
}
