package com.quantumbanking.infra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;

@Configuration
public class TimeZoneConfig {

    @Bean
    public ZoneId transactionZoneId(@Value("${transaction.timezone}") String timezone) {
        return ZoneId.of(timezone);
    }
}