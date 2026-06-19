package com.sport_pro_be.modules.shop.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateShopRequest(
        @NotBlank(message = "Tên cửa hàng không được để trống")
        @Size(max = 150, message = "Tên cửa hàng tối đa 150 ký tự")
        String name,

        String address,

        @DecimalMin(value = "0.0", message = "Đánh giá tối thiểu 0")
        @DecimalMax(value = "5.0", message = "Đánh giá tối đa 5")
        BigDecimal rating,

        @Min(value = 0, message = "Số lượt đánh giá không hợp lệ")
        Integer ratingCount,

        String phone,
        String openingHours,
        String description,
        String logoUrl,
        String coverUrl
) {}
