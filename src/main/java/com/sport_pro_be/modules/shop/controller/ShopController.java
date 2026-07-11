package com.sport_pro_be.modules.shop.controller;

import com.sport_pro_be.common.ApiResponse;
import com.sport_pro_be.common.annotation.RateLimit;
import com.sport_pro_be.modules.shop.constant.ShopConstant;
import com.sport_pro_be.modules.shop.dto.GeocodeResult;
import com.sport_pro_be.modules.shop.dto.ShopResponse;
import com.sport_pro_be.modules.shop.service.IGeocodingService;
import com.sport_pro_be.modules.shop.service.IShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/// Public read of the shop profile (`/api/shop` is in the security whitelist).
@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController {

    private final IShopService shopService;
    private final IGeocodingService geocodingService;

    @GetMapping
    public ApiResponse<ShopResponse> getShop() {
        return ApiResponse.of(ShopConstant.SHOP_RETRIEVED, shopService.getShop());
    }

    /// Forward geocode: turns a typed address into lat/long for the map picker.
    @GetMapping("/geocode")
    @RateLimit(requests = 20, periodInSeconds = 60)
    public ApiResponse<GeocodeResult> geocode(@RequestParam String address) {
        return ApiResponse.of(ShopConstant.GEOCODE_SUCCESS, geocodingService.geocode(address));
    }

    /// Reverse geocode: turns a dropped map pin (lat/long) into a readable address.
    @GetMapping("/reverse-geocode")
    @RateLimit(requests = 20, periodInSeconds = 60)
    public ApiResponse<GeocodeResult> reverseGeocode(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng) {
        return ApiResponse.of(ShopConstant.REVERSE_GEOCODE_SUCCESS, geocodingService.reverseGeocode(lat, lng));
    }
}
