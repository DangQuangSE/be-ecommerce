package com.sport_pro_be.modules.analytics.service;

import com.sport_pro_be.modules.analytics.dto.OrderStatsResponse;
import com.sport_pro_be.modules.analytics.dto.RevenueReportResponse;
import com.sport_pro_be.modules.analytics.dto.TopProductResponse;
import com.sport_pro_be.modules.analytics.dto.TrendingDesignResponse;
import com.sport_pro_be.modules.analytics.interfaces.IAnalyticsService;
import com.sport_pro_be.modules.order.enums.OrderStatus;
import com.sport_pro_be.modules.order.repository.OrderItemRepository;
import com.sport_pro_be.modules.order.repository.OrderRepository;
import com.sport_pro_be.modules.custom_design.repository.CustomDesignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.Clock;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import com.sport_pro_be.exception.BadRequestException;
import com.sport_pro_be.modules.analytics.dto.DailyRevenueProjection;
import com.sport_pro_be.modules.analytics.dto.RevenueSeriesPointResponse;
import com.sport_pro_be.modules.analytics.dto.RevenueSummaryResponse;

@Service
@RequiredArgsConstructor
public class AnalyticsService implements IAnalyticsService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomDesignRepository customDesignRepository;
    private final com.sport_pro_be.modules.auth.repository.UserRepository userRepository;
    private final ZoneId businessZoneId;
    private final Clock clock;

    @Override
    public RevenueSummaryResponse getRevenueSummary(LocalDate startDate, LocalDate endDate) {
        validateRange(startDate, endDate);
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        LocalDate previousEnd = startDate.minusDays(1);
        LocalDate previousStart = previousEnd.minusDays(days - 1);
        List<DailyRevenueProjection> current = queryRange(startDate, endDate);
        List<DailyRevenueProjection> previous = queryRange(previousStart, previousEnd);
        BigDecimal revenue = sumRevenue(current);
        long count = current.stream().mapToLong(DailyRevenueProjection::getOrderCount).sum();
        BigDecimal previousRevenue = sumRevenue(previous);
        BigDecimal average = count == 0 ? BigDecimal.ZERO : revenue.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
        BigDecimal growth = previousRevenue.signum() == 0 ? null
                : revenue.subtract(previousRevenue).multiply(BigDecimal.valueOf(100))
                    .divide(previousRevenue, 2, RoundingMode.HALF_UP);
        String grouping = days <= 31 ? "DAILY" : days <= 180 ? "WEEKLY" : "MONTHLY";
        return new RevenueSummaryResponse(startDate, endDate, revenue, count, average,
                previousRevenue, growth, grouping, buildPoints(startDate, endDate, current, grouping));
    }

    private List<DailyRevenueProjection> queryRange(LocalDate start, LocalDate end) {
        LocalDateTime startUtc = LocalDateTime.ofInstant(start.atStartOfDay(businessZoneId).toInstant(), ZoneOffset.UTC);
        LocalDateTime endUtc = LocalDateTime.ofInstant(end.plusDays(1).atStartOfDay(businessZoneId).toInstant(), ZoneOffset.UTC);
        return orderRepository.aggregateRealizedRevenue(startUtc, endUtc, businessZoneId.getId());
    }

    private List<RevenueSeriesPointResponse> buildPoints(LocalDate start, LocalDate end,
            List<DailyRevenueProjection> rows, String grouping) {
        Map<LocalDate, DailyRevenueProjection> byDate = new HashMap<>();
        rows.forEach(row -> byDate.put(row.getRevenueDate(), row));
        Map<LocalDate, MutableBucket> buckets = new LinkedHashMap<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            LocalDate key = switch (grouping) {
                case "WEEKLY" -> date.with(java.time.DayOfWeek.MONDAY).isBefore(start) ? start : date.with(java.time.DayOfWeek.MONDAY);
                case "MONTHLY" -> date.with(TemporalAdjusters.firstDayOfMonth()).isBefore(start) ? start : date.with(TemporalAdjusters.firstDayOfMonth());
                default -> date;
            };
            MutableBucket bucket = buckets.computeIfAbsent(key, ignored -> new MutableBucket(key));
            bucket.end = date;
            DailyRevenueProjection row = byDate.get(date);
            if (row != null) { bucket.revenue = bucket.revenue.add(row.getRevenue()); bucket.count += row.getOrderCount(); }
        }
        return buckets.values().stream().map(b -> new RevenueSeriesPointResponse(b.start, b.end, b.revenue, b.count)).toList();
    }

    private BigDecimal sumRevenue(List<DailyRevenueProjection> rows) {
        return rows.stream().map(DailyRevenueProjection::getRevenue).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validateRange(LocalDate start, LocalDate end) {
        if (start == null || end == null || start.isAfter(end)) throw new BadRequestException("Invalid date range");
        if (ChronoUnit.DAYS.between(start, end) + 1 > 366L * 5) throw new BadRequestException("Date range cannot exceed 5 years");
    }

    private static final class MutableBucket {
        private final LocalDate start; private LocalDate end; private BigDecimal revenue = BigDecimal.ZERO; private long count;
        private MutableBucket(LocalDate start) { this.start = start; this.end = start; }
    }

    @Override
    public List<RevenueReportResponse> getDailyRevenue(LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        return orderRepository.calculateDailyRevenue(startDateTime, endDateTime);
    }

    @Override
    public List<TopProductResponse> getTopSellingProducts(int limit) {
        return orderItemRepository.findTopSellingProducts(PageRequest.of(0, limit));
    }

    @Override
    public List<TrendingDesignResponse> getTrendingDesigns(int limit) {
        return customDesignRepository.findTrendingDesigns(PageRequest.of(0, limit));
    }

    @Override
    public OrderStatsResponse getOrderStatistics(LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        
        List<Object[]> results = orderRepository.countOrdersByStatus(startDateTime, endDateTime);
        
        Map<OrderStatus, Long> statusCounts = new HashMap<>();
        long totalOrders = 0;
        
        for (Object[] result : results) {
            OrderStatus status = (OrderStatus) result[0];
            Long count = (Long) result[1];
            statusCounts.put(status, count);
            totalOrders += count;
        }
        
        return new OrderStatsResponse(totalOrders, statusCounts);
    }

    @Override
    public com.sport_pro_be.modules.analytics.dto.DashboardSummaryResponse getDashboardSummary() {
        LocalDate today = LocalDate.now(clock.withZone(businessZoneId));
        LocalDateTime now = LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);
        LocalDateTime startOfThisWeek = now.minusDays(7);
        LocalDateTime startOfLastWeek = startOfThisWeek.minusDays(7);

        // This week stats
        java.math.BigDecimal thisWeekRevenue = getRevenueSummary(today.minusDays(6), today).realizedRevenue();
        long thisWeekOrders = orderRepository.countOrdersBetween(startOfThisWeek, now);
        long thisWeekNewCustomers = userRepository.countUsersCreatedBetween(startOfThisWeek, now);

        // Last week stats
        java.math.BigDecimal lastWeekRevenue = getRevenueSummary(today.minusDays(13), today.minusDays(7)).realizedRevenue();
        long lastWeekOrders = orderRepository.countOrdersBetween(startOfLastWeek, startOfThisWeek);
        long lastWeekNewCustomers = userRepository.countUsersCreatedBetween(startOfLastWeek, startOfThisWeek);

        // Calculate growth
        double revenueGrowth = calculateGrowth(thisWeekRevenue.doubleValue(), lastWeekRevenue.doubleValue());
        double ordersGrowth = calculateGrowth(thisWeekOrders, lastWeekOrders);
        double customersGrowth = calculateGrowth(thisWeekNewCustomers, lastWeekNewCustomers);

        return new com.sport_pro_be.modules.analytics.dto.DashboardSummaryResponse(
                thisWeekRevenue,
                revenueGrowth,
                thisWeekOrders,
                ordersGrowth,
                thisWeekNewCustomers,
                customersGrowth
        );
    }

    private double calculateGrowth(double current, double previous) {
        if (previous == 0) {
            return current > 0 ? 100.0 : 0.0;
        }
        double growth = ((current - previous) / previous) * 100.0;
        return Math.round(growth * 10.0) / 10.0; // Round to 1 decimal place
    }
}
