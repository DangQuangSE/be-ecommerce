package com.sport_pro_be.modules.shop.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ShopResponse(
        Long id,
        String name,
        String address,
        BigDecimal rating,
        int ratingCount,
        String phone,
        String openingHours,
        String description,
        String logoUrl,
        String coverUrl
) {}
