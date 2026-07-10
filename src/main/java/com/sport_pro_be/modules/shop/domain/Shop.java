package com.sport_pro_be.modules.shop.domain;

import com.sport_pro_be.common.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/// Single shop/storefront profile, configured by an admin and shown to every
/// user. There is exactly one row (the lowest-id one is treated as canonical).
@Entity
@Table(name = "shop_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shop extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String address;

    /// Store location — latitude (-90..90). Nullable so pre-geo rows survive.
    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    /// Store location — longitude (-180..180). Nullable so pre-geo rows survive.
    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    /// Google Place ID for the store location (optional, for precise directions).
    @Column(name = "place_id", length = 255)
    private String placeId;

    /// Display rating 0.0–5.0 (set manually by the admin).
    @Column(precision = 2, scale = 1)
    private BigDecimal rating;

    @Column(name = "rating_count", nullable = false)
    private int ratingCount;

    @Column(length = 30)
    private String phone;

    @Column(name = "opening_hours", length = 100)
    private String openingHours;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "cover_url")
    private String coverUrl;
}
