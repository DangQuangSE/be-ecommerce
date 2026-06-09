package com.sport_pro_be.modules.payment.vnpay.service;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VnpayServiceTest {

    @Test
    void verifySignature_acceptsValidHash() {
        String secret = "TESTSECRETKEY123456789012345678901234";
        Map<String, String> params = new LinkedHashMap<>();
        params.put("vnp_Amount", "1000000");
        params.put("vnp_Command", "pay");
        params.put("vnp_ResponseCode", "00");
        params.put("vnp_TmnCode", "DEMO");
        params.put("vnp_TxnRef", "1_1700000000000");

        String hash = VnpayHashUtil.signParams(params, secret);
        params.put("vnp_SecureHash", hash);

        VnpayPropertiesStub properties = new VnpayPropertiesStub(secret);
        VnpayService service = new VnpayService(properties, null, null);

        assertTrue(service.verifySignature(params));
    }

    @Test
    void verifySignature_rejectsTamperedHash() {
        String secret = "TESTSECRETKEY123456789012345678901234";
        Map<String, String> params = new LinkedHashMap<>();
        params.put("vnp_Amount", "1000000");
        params.put("vnp_TxnRef", "1_1700000000000");
        params.put("vnp_SecureHash", "invalid");

        VnpayService service = new VnpayService(new VnpayPropertiesStub(secret), null, null);

        assertFalse(service.verifySignature(params));
    }

    private static final class VnpayPropertiesStub extends com.sport_pro_be.modules.payment.vnpay.config.VnpayProperties {
        VnpayPropertiesStub(String hashSecret) {
            setHashSecret(hashSecret);
            setTmnCode("DEMO");
            setPayUrl("https://sandbox.vnpayment.vn/paymentv2/vpcpay.html");
            setReturnUrl("sportpro://payment-result");
            setIpnUrl("http://localhost/api/v1/payments/vnpay/ipn");
        }
    }
}
