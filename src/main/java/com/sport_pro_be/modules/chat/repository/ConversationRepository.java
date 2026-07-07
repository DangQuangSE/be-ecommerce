package com.sport_pro_be.modules.chat.repository;

import com.sport_pro_be.modules.chat.domain.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    List<Conversation> findByCustomerIdOrderByLastMessageAtDesc(Long customerId);

    List<Conversation> findAllByOrderByLastMessageAtDesc();

    @org.springframework.data.jpa.repository.Query("SELECT c FROM Conversation c WHERE c.lastMessage != :excludedMessage ORDER BY c.lastMessageAt DESC")
    List<Conversation> findActiveConversations(@org.springframework.data.repository.query.Param("excludedMessage") String excludedMessage);
}
