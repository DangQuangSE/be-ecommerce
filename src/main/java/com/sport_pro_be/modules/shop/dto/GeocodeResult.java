package com.sport_pro_be.modules.shop.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record GeocodeResult(
        String address,
        BigDecimal latitude,
        BigDecimal longitude
) {}
