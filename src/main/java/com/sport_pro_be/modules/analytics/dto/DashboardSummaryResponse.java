package com.sport_pro_be.modules.analytics.dto;

import java.math.BigDecimal;

public record DashboardSummaryResponse(
        BigDecimal totalRevenue,
        double revenueGrowth,
        long totalOrders,
        double ordersGrowth,
        long newCustomers,
        double customersGrowth
) {
}
