package com.sport_pro_be.modules.shop.service;

import com.sport_pro_be.modules.review.repository.ProductReviewRepository;
import com.sport_pro_be.modules.shop.constant.ShopConstant;
import com.sport_pro_be.modules.shop.domain.Shop;
import com.sport_pro_be.modules.shop.dto.ShopResponse;
import com.sport_pro_be.modules.shop.dto.UpdateShopRequest;
import com.sport_pro_be.modules.shop.repository.ShopRepository;
import com.sport_pro_be.modules.upload.interfaces.IUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements IShopService {

    private final ShopRepository shopRepository;
    private final ProductReviewRepository reviewRepository;
    private final IUploadService uploadService;

    @Override
    @Transactional
    public ShopResponse getShop() {
        Shop shop = getOrCreate();
        double avg = reviewRepository.findAverageRating().orElse(0.0);
        long count = reviewRepository.countActiveReviews();
        shop.setRating(BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP));
        shop.setRatingCount((int) count);
        return toResponse(shopRepository.save(shop));
    }

    @Override
    @Transactional
    public ShopResponse updateShop(UpdateShopRequest request) {
        Shop shop = getOrCreate();
        shop.setName(request.name());
        shop.setAddress(request.address());
        shop.setLatitude(request.latitude());
        shop.setLongitude(request.longitude());
        shop.setPlaceId(request.placeId());
        shop.setPhone(request.phone());
        shop.setOpeningHours(request.openingHours());
        shop.setDescription(request.description());
        shop.setLogoUrl(request.logoUrl());
        shop.setCoverUrl(request.coverUrl());
        return toResponse(shopRepository.save(shop));
    }

    @Override
    public String uploadShopImage(MultipartFile file, String type) {
        return uploadService.uploadFile(file, "shop");
    }

    private Shop getOrCreate() {
        return shopRepository.findTopByOrderByIdAsc().orElseGet(() ->
                shopRepository.save(Shop.builder()
                        .name(ShopConstant.DEFAULT_NAME)
                        .address(ShopConstant.DEFAULT_ADDRESS)
                        .latitude(new BigDecimal(ShopConstant.DEFAULT_LATITUDE))
                        .longitude(new BigDecimal(ShopConstant.DEFAULT_LONGITUDE))
                        .rating(new BigDecimal("5.0"))
                        .ratingCount(0)
                        .phone(ShopConstant.DEFAULT_PHONE)
                        .openingHours(ShopConstant.DEFAULT_OPENING_HOURS)
                        .description(ShopConstant.DEFAULT_DESCRIPTION)
                        .build()));
    }

    private ShopResponse toResponse(Shop s) {
        return ShopResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .address(s.getAddress())
                .latitude(s.getLatitude())
                .longitude(s.getLongitude())
                .placeId(s.getPlaceId())
                .directionsUrl(buildDirectionsUrl(s))
                .rating(s.getRating())
                .ratingCount(s.getRatingCount())
                .phone(s.getPhone())
                .openingHours(s.getOpeningHours())
                .description(s.getDescription())
                .logoUrl(s.getLogoUrl())
                .coverUrl(s.getCoverUrl())
                .build();
    }

    /// Google Maps "directions to" deep link. Prefers lat/long (precise pin);
    /// falls back to the address text when coordinates haven't been set yet.
    private String buildDirectionsUrl(Shop s) {
        String destination;
        if (s.getLatitude() != null && s.getLongitude() != null) {
            destination = s.getLatitude().toPlainString() + "," + s.getLongitude().toPlainString();
        } else if (s.getAddress() != null && !s.getAddress().isBlank()) {
            destination = s.getAddress();
        } else {
            return null;
        }

        StringBuilder url = new StringBuilder(ShopConstant.MAPS_DIRECTIONS_BASE_URL)
                .append(URLEncoder.encode(destination, StandardCharsets.UTF_8));
        if (s.getPlaceId() != null && !s.getPlaceId().isBlank()) {
            url.append(ShopConstant.MAPS_DIRECTIONS_PLACE_ID_PARAM)
                    .append(URLEncoder.encode(s.getPlaceId(), StandardCharsets.UTF_8));
        }
        return url.toString();
    }
}
