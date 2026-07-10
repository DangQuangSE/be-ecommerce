package com.sport_pro_be.modules.shop.service;

import com.sport_pro_be.modules.shop.dto.GeocodeResult;

import java.math.BigDecimal;

public interface IGeocodingService {

    /// Forward geocode: free-text address -> resolved address + lat/long.
    GeocodeResult geocode(String address);

    /// Reverse geocode: lat/long -> resolved address.
    GeocodeResult reverseGeocode(BigDecimal latitude, BigDecimal longitude);
}
