package com.sport_pro_be.modules.analytics.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RevenueSeriesPointResponse(LocalDate bucketStart, LocalDate bucketEnd,
                                         BigDecimal revenue, long orderCount) {}
