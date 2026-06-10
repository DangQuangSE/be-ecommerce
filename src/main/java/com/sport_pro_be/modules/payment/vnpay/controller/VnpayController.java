package com.sport_pro_be.modules.payment.vnpay.controller;

import com.sport_pro_be.common.ApiResponse;
import com.sport_pro_be.common.SecurityUtils;
import com.sport_pro_be.modules.payment.vnpay.config.VnpayProperties;
import com.sport_pro_be.modules.payment.vnpay.constant.VnpayMessageConstant;
import com.sport_pro_be.modules.payment.vnpay.dto.VnpayCreateRequest;
import com.sport_pro_be.modules.payment.vnpay.dto.VnpayCreateResponse;
import com.sport_pro_be.modules.payment.vnpay.dto.VnpayIpnResponse;
import com.sport_pro_be.modules.payment.vnpay.dto.VnpayVerifyResponse;
import com.sport_pro_be.modules.payment.vnpay.service.VnpayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/payments/vnpay")
@RequiredArgsConstructor
public class VnpayController {

    private final VnpayService vnpayService;
    private final VnpayProperties vnpayProperties;

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<VnpayCreateResponse>> createPaymentUrl(
            @Valid @RequestBody VnpayCreateRequest request,
            HttpServletRequest httpRequest) {
        Long userId = SecurityUtils.getCurrentUserId();
        String clientIp = resolveClientIp(httpRequest);
        VnpayCreateResponse response = vnpayService.createPaymentUrl(
                userId, request.getOrderId(), clientIp);
        return ResponseEntity.ok(ApiResponse.of(VnpayMessageConstant.PAYMENT_URL_CREATED, response));
    }

    @GetMapping("/ipn")
    public ResponseEntity<VnpayIpnResponse> handleIpn(HttpServletRequest request) {
        Map<String, String> params = extractParams(request);
        VnpayIpnResponse response = vnpayService.handleIpn(params);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify/{orderId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<VnpayVerifyResponse>> verifyPayment(
            @PathVariable Long orderId) {
        Long userId = SecurityUtils.getCurrentUserId();
        VnpayVerifyResponse response = vnpayService.verifyPayment(userId, orderId);
        return ResponseEntity.ok(ApiResponse.of("Payment status retrieved", response));
    }

    @GetMapping("/bridge-return")
    public ResponseEntity<Void> bridgeReturn(@RequestParam Map<String, String> params) {
        String query = params.entrySet().stream()
                .map(e -> encode(e.getKey()) + "=" + encode(e.getValue()))
                .collect(Collectors.joining("&"));
        String deepLink = vnpayProperties.getReturnUrl() + (query.isEmpty() ? "" : "?" + query);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(deepLink));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    private Map<String, String> extractParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((key, values) -> {
            if (values != null && values.length > 0) {
                params.put(key, values[0]);
            }
        });
        return params;
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static String encode(String value) {
        return java.net.URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
