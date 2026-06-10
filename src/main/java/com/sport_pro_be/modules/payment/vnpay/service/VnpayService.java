package com.sport_pro_be.modules.payment.vnpay.service;

import com.sport_pro_be.exception.BadRequestException;
import com.sport_pro_be.exception.ResourceNotFoundException;
import com.sport_pro_be.modules.order.domain.Order;
import com.sport_pro_be.modules.order.enums.OrderStatus;
import com.sport_pro_be.modules.order.enums.PaymentMethod;
import com.sport_pro_be.modules.order.interfaces.IOrderService;
import com.sport_pro_be.modules.order.repository.OrderRepository;
import com.sport_pro_be.modules.payment.vnpay.config.VnpayProperties;
import com.sport_pro_be.modules.payment.vnpay.constant.VnpayMessageConstant;
import com.sport_pro_be.modules.payment.vnpay.dto.VnpayCreateResponse;
import com.sport_pro_be.modules.payment.vnpay.dto.VnpayIpnResponse;
import com.sport_pro_be.modules.payment.vnpay.dto.VnpayVerifyResponse;
import com.sport_pro_be.modules.payment.vnpay.enums.VnpayPaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VnpayService {

    private static final String VNP_VERSION = "2.1.0";
    private static final String VNP_COMMAND = "pay";
    private static final String VNP_CURR_CODE = "VND";
    private static final String VNP_ORDER_TYPE = "other";
    private static final String VNP_LOCALE = "vn";
    private static final String VNP_RESPONSE_SUCCESS = "00";
    private static final DateTimeFormatter CREATE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final VnpayProperties vnpayProperties;
    private final OrderRepository orderRepository;
    private final IOrderService orderService;

    @Transactional
    public VnpayCreateResponse createPaymentUrl(Long userId, Long orderId, String clientIp) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(VnpayMessageConstant.ORDER_NOT_FOUND));

        validateBankTransferOrder(order, true);

        String txnRef = generateTxnRef(order.getId());
        order.setVnpTxnRef(txnRef);
        orderRepository.save(order);

        String paymentUrl = buildPaymentUrl(order, txnRef, clientIp);
        return VnpayCreateResponse.builder()
                .orderId(order.getId())
                .txnRef(txnRef)
                .paymentUrl(paymentUrl)
                .build();
    }

    @Transactional
    public VnpayIpnResponse handleIpn(Map<String, String> params) {
        log.info("VNPay IPN received: {}", params);

        if (!verifySignature(params)) {
            log.warn("VNPay IPN invalid signature for txnRef={}", params.get("vnp_TxnRef"));
            return VnpayIpnResponse.error("97", "Invalid Checksum");
        }

        String txnRef = params.get("vnp_TxnRef");
        if (txnRef == null || txnRef.isBlank()) {
            return VnpayIpnResponse.error("01", "Order not Found");
        }

        Order order = orderRepository.findByVnpTxnRef(txnRef)
                .orElse(null);
        if (order == null) {
            log.warn("VNPay IPN order not found for txnRef={}", txnRef);
            return VnpayIpnResponse.error("01", "Order not Found");
        }

        String responseCode = params.get("vnp_ResponseCode");
        if (VNP_RESPONSE_SUCCESS.equals(responseCode)) {
            if (order.getStatus() == OrderStatus.CONFIRMED) {
                return VnpayIpnResponse.success();
            }
            if (!verifyAmount(order, params.get("vnp_Amount"))) {
                log.warn("VNPay IPN amount mismatch for orderId={}", order.getId());
                return VnpayIpnResponse.error("04", "Invalid Amount");
            }
            orderService.fulfillOrder(order.getId());
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);
            return VnpayIpnResponse.success();
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            return VnpayIpnResponse.success();
        }
        if (order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        }
        return VnpayIpnResponse.success();
    }

    @Transactional(readOnly = true)
    public VnpayVerifyResponse verifyPayment(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(VnpayMessageConstant.ORDER_NOT_FOUND));

        validateBankTransferOrder(order, false);

        return VnpayVerifyResponse.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .paymentStatus(mapPaymentStatus(order.getStatus()))
                .txnRef(order.getVnpTxnRef())
                .build();
    }

    private void validateBankTransferOrder(Order order, boolean requirePending) {
        if (order.getPaymentMethod() != PaymentMethod.BANK_TRANSFER) {
            throw new BadRequestException(VnpayMessageConstant.INVALID_PAYMENT_METHOD);
        }
        if (requirePending && order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException(VnpayMessageConstant.INVALID_ORDER_STATUS);
        }
        if (order.getTotalAmount() == null || order.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException(VnpayMessageConstant.ORDER_AMOUNT_INVALID);
        }
    }

    public boolean verifySignature(Map<String, String> params) {
        String receivedHash = params.get("vnp_SecureHash");
        if (receivedHash == null || receivedHash.isBlank()) {
            return false;
        }
        Map<String, String> signParams = new HashMap<>(params);
        signParams.remove("vnp_SecureHash");
        signParams.remove("vnp_SecureHashType");
        String computed = VnpayHashUtil.signParams(signParams, vnpayProperties.getHashSecret());
        return computed.equalsIgnoreCase(receivedHash);
    }

    public String generateTxnRef(Long orderId) {
        return orderId + "_" + System.currentTimeMillis();
    }

    public String buildPaymentUrl(Order order, String txnRef, String clientIp) {
        long amount = order.getTotalAmount()
                .multiply(BigDecimal.valueOf(100))
                .longValueExact();

        Map<String, String> params = new LinkedHashMap<>();
        params.put("vnp_Version", VNP_VERSION);
        params.put("vnp_Command", VNP_COMMAND);
        params.put("vnp_TmnCode", vnpayProperties.getTmnCode());
        params.put("vnp_Amount", String.valueOf(amount));
        params.put("vnp_CurrCode", VNP_CURR_CODE);
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", "Thanh toan don hang #" + order.getId());
        params.put("vnp_OrderType", VNP_ORDER_TYPE);
        params.put("vnp_Locale", VNP_LOCALE);
        params.put("vnp_ReturnUrl", vnpayProperties.getReturnUrl());
        params.put("vnp_IpAddr", clientIp != null && !clientIp.isBlank() ? clientIp : "127.0.0.1");
        params.put("vnp_CreateDate", LocalDateTime.now().format(CREATE_DATE_FORMAT));

        String secureHash = VnpayHashUtil.signParams(params, vnpayProperties.getHashSecret());
        params.put("vnp_SecureHash", secureHash);

        String query = params.entrySet().stream()
                .map(entry -> encodeParam(entry.getKey()) + "=" + encodeParam(entry.getValue()))
                .collect(Collectors.joining("&"));

        return vnpayProperties.getPayUrl() + "?" + query;
    }

    private boolean verifyAmount(Order order, String vnpAmount) {
        if (vnpAmount == null || vnpAmount.isBlank()) {
            return false;
        }
        long expected = order.getTotalAmount()
                .multiply(BigDecimal.valueOf(100))
                .longValueExact();
        try {
            return expected == Long.parseLong(vnpAmount);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static VnpayPaymentStatus mapPaymentStatus(OrderStatus status) {
        return switch (status) {
            case CONFIRMED, PROCESSING, SHIPPED, DELIVERED -> VnpayPaymentStatus.SUCCESS;
            case CANCELLED -> VnpayPaymentStatus.FAILED;
            default -> VnpayPaymentStatus.PENDING;
        };
    }

    private static String encodeParam(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
