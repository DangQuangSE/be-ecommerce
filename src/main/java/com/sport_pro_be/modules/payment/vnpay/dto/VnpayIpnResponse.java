package com.sport_pro_be.modules.payment.vnpay.dto;

import lombok.Value;

@Value
public class VnpayIpnResponse {

    String rspCode;
    String message;

    public static VnpayIpnResponse success() {
        return new VnpayIpnResponse("00", "Confirm Success");
    }

    public static VnpayIpnResponse error(String code, String message) {
        return new VnpayIpnResponse(code, message);
    }
}
