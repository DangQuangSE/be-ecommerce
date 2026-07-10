package com.sport_pro_be.modules.shop.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateShopRequest(
        @NotBlank(message = "Tên cửa hàng không được để trống")
        @Size(max = 150, message = "Tên cửa hàng tối đa 150 ký tự")
        String name,

        String address,

        @DecimalMin(value = "-90.0", message = "Vĩ độ không hợp lệ")
        @DecimalMax(value = "90.0", message = "Vĩ độ không hợp lệ")
        BigDecimal latitude,

        @DecimalMin(value = "-180.0", message = "Kinh độ không hợp lệ")
        @DecimalMax(value = "180.0", message = "Kinh độ không hợp lệ")
        BigDecimal longitude,

        @Size(max = 255, message = "Mã địa điểm tối đa 255 ký tự")
        String placeId,

        String phone,
        String openingHours,
        String description,
        String logoUrl,
        String coverUrl
) {}
