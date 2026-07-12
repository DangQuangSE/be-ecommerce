package com.sport_pro_be.modules.analytics.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RevenueSummaryResponse(
        LocalDate startDate, LocalDate endDate, BigDecimal realizedRevenue,
        long orderCount, BigDecimal averageOrderValue, BigDecimal previousPeriodRevenue,
        BigDecimal growthPercent, String grouping, List<RevenueSeriesPointResponse> points) {}
