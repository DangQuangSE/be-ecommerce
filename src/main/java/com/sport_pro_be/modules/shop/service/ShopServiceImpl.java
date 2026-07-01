package com.sport_pro_be.modules.shop.service;

import com.sport_pro_be.modules.shop.domain.Shop;
import com.sport_pro_be.modules.shop.dto.ShopResponse;
import com.sport_pro_be.modules.shop.dto.UpdateShopRequest;
import com.sport_pro_be.modules.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements IShopService {

    private final ShopRepository shopRepository;

    @Override
    @Transactional
    public ShopResponse getShop() {
        return toResponse(getOrCreate());
    }

    @Override
    @Transactional
    public ShopResponse updateShop(UpdateShopRequest request) {
        Shop shop = getOrCreate();
        shop.setName(request.name());
        shop.setAddress(request.address());
        shop.setRating(request.rating());
        shop.setRatingCount(request.ratingCount() == null ? 0 : request.ratingCount());
        shop.setPhone(request.phone());
        shop.setOpeningHours(request.openingHours());
        shop.setDescription(request.description());
        shop.setLogoUrl(request.logoUrl());
        shop.setCoverUrl(request.coverUrl());
        return toResponse(shopRepository.save(shop));
    }

    private Shop getOrCreate() {
        // No shop configured yet: create an empty placeholder (all fields null,
        // ratingCount defaults to 0) for the admin to fill in later.
        return shopRepository.findTopByOrderByIdAsc()
                .orElseGet(() -> shopRepository.save(Shop.builder().build()));
    }

    private ShopResponse toResponse(Shop s) {
        return ShopResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .address(s.getAddress())
                .rating(s.getRating())
                .ratingCount(s.getRatingCount())
                .phone(s.getPhone())
                .openingHours(s.getOpeningHours())
                .description(s.getDescription())
                .logoUrl(s.getLogoUrl())
                .coverUrl(s.getCoverUrl())
                .build();
    }
}
