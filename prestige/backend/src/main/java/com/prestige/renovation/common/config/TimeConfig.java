package com.prestige.renovation.common.config;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides a {@link Clock} in the business time zone (Asia/Singapore by default).
 * Timestamps are always stored in UTC; the zone is only used for calendar-day logic
 * (view de-duplication, report periods, "today" for availability).
 */
@Configuration
public class TimeConfig {

    @Bean
    public Clock clock(AppProperties properties) {
        String zone = properties.zone() == null ? "Asia/Singapore" : properties.zone();
        return Clock.system(ZoneId.of(zone));
    }
}
