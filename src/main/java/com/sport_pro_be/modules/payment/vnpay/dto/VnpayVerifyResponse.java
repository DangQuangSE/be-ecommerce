package com.sport_pro_be.modules.payment.vnpay.dto;

import com.sport_pro_be.modules.order.enums.OrderStatus;
import com.sport_pro_be.modules.payment.vnpay.enums.VnpayPaymentStatus;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class VnpayVerifyResponse {

    Long orderId;
    OrderStatus status;
    VnpayPaymentStatus paymentStatus;
    String txnRef;
    String vnpResponseCode;
}
