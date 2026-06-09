package com.sport_pro_be.modules.payment.vnpay.service;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class VnpayHashUtilTest {

    @Test
    void buildHashData_sortsFieldsAlphabetically() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("vnp_TxnRef", "1_123");
        params.put("vnp_Amount", "100000");
        params.put("vnp_Command", "pay");

        String hashData = VnpayHashUtil.buildHashData(params);

        assertEquals("vnp_Amount=100000&vnp_Command=pay&vnp_TxnRef=1_123", hashData);
    }

    @Test
    void signParams_isDeterministic() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("vnp_Amount", "1000000");
        params.put("vnp_Command", "pay");
        params.put("vnp_CreateDate", "20240101120000");
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_IpAddr", "127.0.0.1");
        params.put("vnp_Locale", "vn");
        params.put("vnp_OrderInfo", "Thanh toan don hang #1");
        params.put("vnp_OrderType", "other");
        params.put("vnp_ReturnUrl", "sportpro://payment-result");
        params.put("vnp_TmnCode", "DEMOV210");
        params.put("vnp_TxnRef", "1_1700000000000");
        params.put("vnp_Version", "2.1.0");

        String secret = "RAOEXHYVSDDIIENYWSLDIYYZUHIZQYUW";
        String hash1 = VnpayHashUtil.signParams(params, secret);
        String hash2 = VnpayHashUtil.signParams(params, secret);

        assertEquals(hash1, hash2);
        assertEquals(128, hash1.length());
    }

    @Test
    void signParams_matchesVnpayOfficialDocExample() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("vnp_Amount", "1806000");
        params.put("vnp_Command", "pay");
        params.put("vnp_CreateDate", "20210801153333");
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_IpAddr", "127.0.0.1");
        params.put("vnp_Locale", "vn");
        params.put("vnp_OrderInfo", "Thanh toan don hang :5");
        params.put("vnp_OrderType", "other");
        params.put("vnp_ReturnUrl", "https://domainmerchant.vn/ReturnUrl");
        params.put("vnp_TmnCode", "DEMOV210");
        params.put("vnp_TxnRef", "5");
        params.put("vnp_Version", "2.1.0");

        String secret = "RAOEXHYVSDDIIENYWSLDIYYZUHIZQYUW";
        String expected = "3e0d61a0c0534b2e36680b3f7277743e8784cc4e1d68fa7d276e79c23be7d6318d338b477910a27992f5057bb1582bd44bd82ae8009ffaf6d141219218625c42";

        assertEquals(expected, VnpayHashUtil.signParams(params, secret));
    }

    @Test
    void signParams_changesWhenSecretChanges() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("vnp_Amount", "100000");
        params.put("vnp_Command", "pay");

        String hashA = VnpayHashUtil.signParams(params, "secret-a");
        String hashB = VnpayHashUtil.signParams(params, "secret-b");

        assertNotEquals(hashA, hashB);
    }
}
