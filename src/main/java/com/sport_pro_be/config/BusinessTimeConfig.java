package com.sport_pro_be.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.ZoneId;

@Configuration
public class BusinessTimeConfig {

    @Bean
    public ZoneId businessZoneId(@Value("${app.business-zone}") String zoneId) {
        try {
            return ZoneId.of(zoneId);
        } catch (DateTimeException exception) {
            throw new IllegalStateException("Invalid app.business-zone: " + zoneId, exception);
        }
    }

    @Bean
    public Clock applicationClock() {
        return Clock.systemUTC();
    }
}
