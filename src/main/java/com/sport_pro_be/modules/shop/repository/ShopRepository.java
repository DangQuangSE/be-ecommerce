package com.sport_pro_be.modules.shop.repository;

import com.sport_pro_be.modules.shop.domain.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    /// The canonical shop profile is the lowest-id row.
    Optional<Shop> findTopByOrderByIdAsc();
}
