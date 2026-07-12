package com.sport_pro_be.modules.order.repository;

import com.sport_pro_be.modules.order.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.sport_pro_be.modules.analytics.dto.DailyRevenueProjection;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Order o where o.id = :id")
    Optional<Order> findByIdForUpdate(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Order o where o.vnpTxnRef = :vnpTxnRef")
    Optional<Order> findByVnpTxnRefForUpdate(String vnpTxnRef);

    Page<Order> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"items", "items.productVariant", "items.productVariant.product"})
    Optional<Order> findByIdAndUserId(Long id, Long userId);

    @EntityGraph(attributePaths = {
            "items",
            "items.productVariant",
            "items.productVariant.product",
            "items.customDesign",
            "user",
            "coupon"
    })
    Optional<Order> findByVnpTxnRef(String vnpTxnRef);

    @EntityGraph(attributePaths = {
            "items",
            "items.productVariant",
            "items.productVariant.product",
            "items.customDesign",
            "user",
            "coupon"
    })
    Optional<Order> findFulfillmentGraphById(Long id);

    @org.springframework.data.jpa.repository.Query(
        "SELECT o FROM Order o WHERE " +
        "(:status IS NULL OR o.status = :status) AND " +
        "(:search = '' OR o.phoneNumber LIKE CONCAT('%', :search, '%') " +
        "OR CAST(o.id AS string) LIKE CONCAT('%', :search, '%') " +
        "OR o.shippingAddress LIKE CONCAT('%', :search, '%'))"
    )
    Page<Order> searchOrders(
            @org.springframework.data.repository.query.Param("search") String search,
            @org.springframework.data.repository.query.Param("status") com.sport_pro_be.modules.order.enums.OrderStatus status,
            Pageable pageable);


    @org.springframework.data.jpa.repository.Query("SELECT o.status, COUNT(o) FROM Order o " +
           "WHERE o.createdAt >= :start AND o.createdAt <= :end " +
           "GROUP BY o.status")
    java.util.List<Object[]> countOrdersByStatus(
            @org.springframework.data.repository.query.Param("start") java.time.LocalDateTime start,
            @org.springframework.data.repository.query.Param("end") java.time.LocalDateTime end);


    @org.springframework.data.jpa.repository.Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= :start AND o.createdAt <= :end")
    long countOrdersBetween(
            @org.springframework.data.repository.query.Param("start") java.time.LocalDateTime start,
            @org.springframework.data.repository.query.Param("end") java.time.LocalDateTime end);

    @Query(value = """
            SELECT ((delivered_at AT TIME ZONE 'UTC') AT TIME ZONE :businessZone)::date AS revenueDate,
                   COALESCE(SUM(total_amount), 0) AS revenue,
                   COUNT(*) AS orderCount
              FROM orders
             WHERE status = 'DELIVERED' AND payment_completed = TRUE AND delivered_at IS NOT NULL
               AND delivered_at >= :startUtc AND delivered_at < :endUtc
             GROUP BY revenueDate ORDER BY revenueDate
            """, nativeQuery = true)
    java.util.List<DailyRevenueProjection> aggregateRealizedRevenue(
            @org.springframework.data.repository.query.Param("startUtc") java.time.LocalDateTime startUtc,
            @org.springframework.data.repository.query.Param("endUtc") java.time.LocalDateTime endUtc,
            @org.springframework.data.repository.query.Param("businessZone") String businessZone);
}
