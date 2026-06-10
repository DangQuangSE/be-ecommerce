package com.sport_pro_be.modules.payment.vnpay.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class VnpayCreateResponse {

    Long orderId;
    String txnRef;
    String paymentUrl;
}
