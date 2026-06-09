package com.sport_pro_be.modules.payment.vnpay.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VnpayCreateRequest {

    @NotNull(message = "orderId is required")
    private Long orderId;
}
