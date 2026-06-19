package com.sport_pro_be.modules.shop.controller;

import com.sport_pro_be.common.ApiResponse;
import com.sport_pro_be.modules.shop.constant.ShopConstant;
import com.sport_pro_be.modules.shop.dto.ShopResponse;
import com.sport_pro_be.modules.shop.service.IShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// Public read of the shop profile (`/api/shop` is in the security whitelist).
@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController {

    private final IShopService shopService;

    @GetMapping
    public ApiResponse<ShopResponse> getShop() {
        return ApiResponse.of(ShopConstant.SHOP_RETRIEVED, shopService.getShop());
    }
}
