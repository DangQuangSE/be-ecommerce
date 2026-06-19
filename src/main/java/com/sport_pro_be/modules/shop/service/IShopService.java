package com.sport_pro_be.modules.shop.service;

import com.sport_pro_be.modules.shop.dto.ShopResponse;
import com.sport_pro_be.modules.shop.dto.UpdateShopRequest;

public interface IShopService {

    /// The shop profile shown to users (created with defaults on first access).
    ShopResponse getShop();

    /// Admin updates the shop profile.
    ShopResponse updateShop(UpdateShopRequest request);
}
