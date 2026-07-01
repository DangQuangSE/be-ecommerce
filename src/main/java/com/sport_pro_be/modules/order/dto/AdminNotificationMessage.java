package com.sport_pro_be.modules.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminNotificationMessage {
    private Long orderId;
    private String customerName;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private String message;
}
