package com.sport_pro_be.modules.shop.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sport_pro_be.exception.AppException;
import com.sport_pro_be.exception.BadRequestException;
import com.sport_pro_be.exception.ResourceNotFoundException;
import com.sport_pro_be.modules.shop.dto.GeocodeResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MapboxGeocodingService implements IGeocodingService {

    private static final String GEOCODING_PATH = "/geocoding/v5/mapbox.places/{query}.json";

    private final RestClient mapboxRestClient;

    @Value("${mapbox.access-token}")
    private String accessToken;

    @Override
    public GeocodeResult geocode(String address) {
        if (address == null || address.isBlank()) {
            throw new BadRequestException("Địa chỉ không được để trống");
        }
        return callMapbox(address);
    }

    @Override
    public GeocodeResult reverseGeocode(BigDecimal latitude, BigDecimal longitude) {
        if (latitude == null || longitude == null) {
            throw new BadRequestException("Vĩ độ và kinh độ không được để trống");
        }
        if (latitude.compareTo(BigDecimal.valueOf(-90)) < 0 || latitude.compareTo(BigDecimal.valueOf(90)) > 0) {
            throw new BadRequestException("Vĩ độ không hợp lệ");
        }
        if (longitude.compareTo(BigDecimal.valueOf(-180)) < 0 || longitude.compareTo(BigDecimal.valueOf(180)) > 0) {
            throw new BadRequestException("Kinh độ không hợp lệ");
        }
        // Mapbox reverse geocoding takes the coordinate pair as "longitude,latitude".
        return callMapbox(longitude.toPlainString() + "," + latitude.toPlainString());
    }

    private GeocodeResult callMapbox(String query) {
        MapboxGeocodingResponse response;
        try {
            response = mapboxRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(GEOCODING_PATH)
                            .queryParam("access_token", accessToken)
                            .build(query))
                    .retrieve()
                    .body(MapboxGeocodingResponse.class);
        } catch (RestClientException e) {
            // Don't log the exception itself: RestClient embeds the full request URI
            // (including access_token) in its message on connection/timeout failures.
            log.error("Mapbox geocoding request failed: {}", e.getClass().getSimpleName());
            throw new AppException("Không thể kết nối tới dịch vụ bản đồ", HttpStatus.BAD_GATEWAY);
        }

        if (response == null || response.features() == null || response.features().isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy vị trí phù hợp");
        }

        MapboxFeature feature = response.features().get(0);
        double[] center = feature.center();
        return GeocodeResult.builder()
                .address(feature.placeName())
                .latitude(BigDecimal.valueOf(center[1]))
                .longitude(BigDecimal.valueOf(center[0]))
                .build();
    }

    private record MapboxGeocodingResponse(List<MapboxFeature> features) {}

    private record MapboxFeature(
            @JsonProperty("place_name") String placeName,
            double[] center // Mapbox order: [longitude, latitude]
    ) {}
}
