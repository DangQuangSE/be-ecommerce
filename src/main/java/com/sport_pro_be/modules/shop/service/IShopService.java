package com.sport_pro_be.modules.shop.service;

import com.sport_pro_be.modules.shop.dto.ShopResponse;
import com.sport_pro_be.modules.shop.dto.UpdateShopRequest;
import org.springframework.web.multipart.MultipartFile;

public interface IShopService {

    /// The shop profile shown to users (created with defaults on first access).
    ShopResponse getShop();

    /// Admin updates the shop profile.
    ShopResponse updateShop(UpdateShopRequest request);

    /// Uploads a shop image (logo or cover) to Cloudinary and returns the URL.
    String uploadShopImage(MultipartFile file, String type);
}
