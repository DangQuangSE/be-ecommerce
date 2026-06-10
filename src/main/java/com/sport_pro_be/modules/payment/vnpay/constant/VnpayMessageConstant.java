package com.sport_pro_be.modules.payment.vnpay.constant;

public final class VnpayMessageConstant {

    private VnpayMessageConstant() {
    }

    public static final String PAYMENT_URL_CREATED = "VNPay payment URL created successfully";
    public static final String ORDER_NOT_FOUND = "Order not found";
    public static final String ORDER_NOT_OWNED = "You do not have access to this order";
    public static final String INVALID_PAYMENT_METHOD = "Order payment method must be BANK_TRANSFER";
    public static final String INVALID_ORDER_STATUS = "Order must be in PENDING status";
    public static final String ORDER_AMOUNT_INVALID = "Order total amount must be greater than zero";
}
