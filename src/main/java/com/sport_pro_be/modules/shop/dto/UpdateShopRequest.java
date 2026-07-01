package com.sport_pro_be.modules.shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateShopRequest(
        @NotBlank(message = "Tên cửa hàng không được để trống")
        @Size(max = 150, message = "Tên cửa hàng tối đa 150 ký tự")
        String name,

        String address,
        String phone,
        String openingHours,
        String description,
        String logoUrl,
        String coverUrl
) {}
