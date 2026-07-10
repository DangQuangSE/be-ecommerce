package com.sport_pro_be.modules.shop.service;

import com.sport_pro_be.modules.review.repository.ProductReviewRepository;
import com.sport_pro_be.modules.shop.constant.ShopConstant;
import com.sport_pro_be.modules.shop.domain.Shop;
import com.sport_pro_be.modules.shop.dto.ShopResponse;
import com.sport_pro_be.modules.shop.dto.UpdateShopRequest;
import com.sport_pro_be.modules.shop.repository.ShopRepository;
import com.sport_pro_be.modules.upload.interfaces.IUploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/// Unit tests for {@link ShopServiceImpl}, focused on the private
/// buildDirectionsUrl(Shop) branching logic exercised indirectly through
/// getShop()/updateShop().
@ExtendWith(MockitoExtension.class)
class ShopServiceImplTest {

    private static final String BASE_URL = "https://www.google.com/maps/dir/?api=1&destination=";

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private ProductReviewRepository reviewRepository;

    @Mock
    private IUploadService uploadService;

    private ShopServiceImpl shopService;

    @BeforeEach
    void setUp() {
        shopService = new ShopServiceImpl(shopRepository, reviewRepository, uploadService);
        // Every scenario below eventually persists the shop; echo back whatever was passed in.
        when(shopRepository.save(any(Shop.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private void stubReviewAggregates() {
        when(reviewRepository.findAverageRating()).thenReturn(Optional.of(4.5));
        when(reviewRepository.countActiveReviews()).thenReturn(3L);
    }

    @Test
    void getShop_withLatitudeAndLongitudeSet_buildsCoordinateDirectionsUrl() {
        Shop existing = Shop.builder()
                .id(1L)
                .name("Sport Pro")
                .latitude(new BigDecimal("10.7295"))
                .longitude(new BigDecimal("106.7215"))
                .build();
        when(shopRepository.findTopByOrderByIdAsc()).thenReturn(Optional.of(existing));
        stubReviewAggregates();

        ShopResponse response = shopService.getShop();

        assertEquals(BASE_URL + encode("10.7295,106.7215"), response.directionsUrl());
    }

    @Test
    void getShop_withCoordinatesAndPlaceId_appendsDestinationPlaceId() {
        Shop existing = Shop.builder()
                .id(1L)
                .latitude(new BigDecimal("10.7295"))
                .longitude(new BigDecimal("106.7215"))
                .placeId("ChIJ123abcXYZ")
                .build();
        when(shopRepository.findTopByOrderByIdAsc()).thenReturn(Optional.of(existing));
        stubReviewAggregates();

        ShopResponse response = shopService.getShop();

        String expected = BASE_URL + encode("10.7295,106.7215")
                + "&destination_place_id=" + encode("ChIJ123abcXYZ");
        assertEquals(expected, response.directionsUrl());
    }

    @Test
    void getShop_withoutCoordinatesButWithAddress_fallsBackToAddress() {
        Shop existing = Shop.builder()
                .id(1L)
                .latitude(null)
                .longitude(null)
                .address("123 Nguyễn Văn Linh, Quận 7, TP. Hồ Chí Minh")
                .build();
        when(shopRepository.findTopByOrderByIdAsc()).thenReturn(Optional.of(existing));
        stubReviewAggregates();

        ShopResponse response = shopService.getShop();

        assertEquals(
                BASE_URL + encode("123 Nguyễn Văn Linh, Quận 7, TP. Hồ Chí Minh"),
                response.directionsUrl());
    }

    @Test
    void getShop_withOnlyOneOfLatitudeOrLongitudeSet_fallsBackToAddress() {
        Shop existing = Shop.builder()
                .id(1L)
                .latitude(new BigDecimal("10.7295"))
                .longitude(null)
                .address("Fallback Address")
                .build();
        when(shopRepository.findTopByOrderByIdAsc()).thenReturn(Optional.of(existing));
        stubReviewAggregates();

        ShopResponse response = shopService.getShop();

        assertEquals(BASE_URL + encode("Fallback Address"), response.directionsUrl());
    }

    @Test
    void getShop_withNoCoordinatesAndNoAddress_directionsUrlIsNull() {
        Shop existing = Shop.builder()
                .id(1L)
                .latitude(null)
                .longitude(null)
                .address(null)
                .build();
        when(shopRepository.findTopByOrderByIdAsc()).thenReturn(Optional.of(existing));
        stubReviewAggregates();

        ShopResponse response = shopService.getShop();

        assertNull(response.directionsUrl());
    }

    @Test
    void getShop_withBlankAddressAndNoCoordinates_directionsUrlIsNull() {
        Shop existing = Shop.builder()
                .id(1L)
                .latitude(null)
                .longitude(null)
                .address("   ")
                .build();
        when(shopRepository.findTopByOrderByIdAsc()).thenReturn(Optional.of(existing));
        stubReviewAggregates();

        ShopResponse response = shopService.getShop();

        assertNull(response.directionsUrl());
    }

    @Test
    void getShop_withBlankPlaceId_doesNotAppendPlaceIdParam() {
        Shop existing = Shop.builder()
                .id(1L)
                .latitude(new BigDecimal("10.7295"))
                .longitude(new BigDecimal("106.7215"))
                .placeId("   ")
                .build();
        when(shopRepository.findTopByOrderByIdAsc()).thenReturn(Optional.of(existing));
        stubReviewAggregates();

        ShopResponse response = shopService.getShop();

        assertEquals(BASE_URL + encode("10.7295,106.7215"), response.directionsUrl());
    }

    @Test
    void getShop_whenNoShopRowExists_seedsDefaultsAndBuildsCoordinateDirectionsUrl() {
        when(shopRepository.findTopByOrderByIdAsc()).thenReturn(Optional.empty());
        stubReviewAggregates();

        ShopResponse response = shopService.getShop();

        String expectedDestination = ShopConstant.DEFAULT_LATITUDE + "," + ShopConstant.DEFAULT_LONGITUDE;
        assertEquals(BASE_URL + encode(expectedDestination), response.directionsUrl());
    }

    @Test
    void updateShop_withNewCoordinatesAndPlaceId_buildsDirectionsUrlFromRequestValues() {
        Shop existing = Shop.builder()
                .id(2L)
                .latitude(new BigDecimal("9.9999999"))
                .longitude(new BigDecimal("9.9999999"))
                .placeId("old-place-id")
                .build();
        when(shopRepository.findTopByOrderByIdAsc()).thenReturn(Optional.of(existing));

        UpdateShopRequest request = new UpdateShopRequest(
                "New Name",
                "New Address",
                new BigDecimal("1.234567"),
                new BigDecimal("2.345678"),
                "new-place-id",
                "0909000000",
                "09:00 - 18:00",
                "desc",
                "logo.png",
                "cover.png");

        ShopResponse response = shopService.updateShop(request);

        String expected = BASE_URL + encode("1.234567,2.345678")
                + "&destination_place_id=" + encode("new-place-id");
        assertEquals(expected, response.directionsUrl());
        assertEquals(new BigDecimal("1.234567"), response.latitude());
        assertEquals(new BigDecimal("2.345678"), response.longitude());
    }

    @Test
    void updateShop_withNoCoordinatesAndBlankAddress_directionsUrlIsNull() {
        Shop existing = Shop.builder().id(2L).build();
        when(shopRepository.findTopByOrderByIdAsc()).thenReturn(Optional.of(existing));

        UpdateShopRequest request = new UpdateShopRequest(
                "New Name",
                "",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        ShopResponse response = shopService.updateShop(request);

        assertNull(response.directionsUrl());
    }
}
