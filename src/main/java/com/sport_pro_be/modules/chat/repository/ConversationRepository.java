package com.sport_pro_be.modules.chat.repository;

import com.sport_pro_be.modules.chat.domain.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    List<Conversation> findByCustomerIdOrderByLastMessageAtDesc(Long customerId);

    List<Conversation> findAllByOrderByLastMessageAtDesc();
}
