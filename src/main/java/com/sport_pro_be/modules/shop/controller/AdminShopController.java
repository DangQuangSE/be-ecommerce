package com.sport_pro_be.modules.shop.controller;

import com.sport_pro_be.common.ApiResponse;
import com.sport_pro_be.modules.shop.constant.ShopConstant;
import com.sport_pro_be.modules.shop.dto.ShopResponse;
import com.sport_pro_be.modules.shop.dto.UpdateShopRequest;
import com.sport_pro_be.modules.shop.service.IShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/// Admin configuration of the shop profile (`/api/admin/**` requires ADMIN).
@RestController
@RequestMapping("/api/admin/shop")
@RequiredArgsConstructor
public class AdminShopController {

    private final IShopService shopService;

    @PutMapping
    public ApiResponse<ShopResponse> updateShop(@Valid @RequestBody UpdateShopRequest request) {
        return ApiResponse.of(ShopConstant.SHOP_UPDATED, shopService.updateShop(request));
    }

    @PostMapping("/upload-image")
    public ApiResponse<Map<String, String>> uploadShopImage(
            @RequestParam MultipartFile file,
            @RequestParam String type) {
        String url = shopService.uploadShopImage(file, type);
        return ApiResponse.of(ShopConstant.SHOP_IMAGE_UPLOADED, Map.of("url", url));
    }
}
