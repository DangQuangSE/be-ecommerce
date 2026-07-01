package com.sport_pro_be.modules.shop.controller;

import com.sport_pro_be.common.ApiResponse;
import com.sport_pro_be.modules.shop.dto.ShopResponse;
import com.sport_pro_be.modules.shop.dto.UpdateShopRequest;
import com.sport_pro_be.modules.shop.service.IShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// Admin configuration of the shop profile (`/api/admin/**` requires ADMIN).
@RestController
@RequestMapping("/api/admin/shop")
@RequiredArgsConstructor
public class AdminShopController {

    private final IShopService shopService;

    @PutMapping
    public ApiResponse<ShopResponse> updateShop(@Valid @RequestBody UpdateShopRequest request) {
        return ApiResponse.of("Cập nhật thông tin cửa hàng thành công", shopService.updateShop(request));
    }
}
