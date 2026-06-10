package com.sport_pro_be.modules.payment.vnpay.service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class VnpayHashUtil {

    private static final String HMAC_SHA512 = "HmacSHA512";

    private VnpayHashUtil() {
    }

    public static String hmacSha512(String data, String secretKey) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA512);
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    secretKey.getBytes(StandardCharsets.UTF_8), HMAC_SHA512);
            mac.init(secretKeySpec);
            byte[] hashBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hashBytes.length * 2);
            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("Failed to compute VNPay HMAC-SHA512", e);
        }
    }

    public static String buildHashData(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        boolean first = true;
        for (String fieldName : fieldNames) {
            String fieldValue = params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                if (!first) {
                    hashData.append('&');
                }
                hashData.append(fieldName)
                        .append('=')
                        .append(encodeHashValue(fieldValue));
                first = false;
            }
        }
        return hashData.toString();
    }

    public static String signParams(Map<String, String> params, String hashSecret) {
        return hmacSha512(buildHashData(params), hashSecret);
    }

    private static String encodeHashValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
