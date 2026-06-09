package com.sport_pro_be.modules.payment.vnpay.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "vnpay")
public class VnpayProperties {

    @NotBlank
    private String tmnCode;

    @NotBlank
    private String hashSecret;

    @NotBlank
    private String payUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    @NotBlank
    private String returnUrl = "sportpro://payment-result";

    @NotBlank
    private String ipnUrl;
}
