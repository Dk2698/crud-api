package com.kumar.crudapi.trace;

import de.mkammerer.snowflakeid.SnowflakeIdGenerator;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class TradeIdGenerator {

    private final SnowflakeIdGenerator snowflake;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    public TradeIdGenerator() {
        snowflake = SnowflakeIdGenerator.createDefault(1);
    }

    // Generate unique internal ID
    public long generateUniqueId() {
        return snowflake.next();
    }

    // Generate audit-friendly trade ID
    public String generateTradeId(String serviceCode) {
        long uniqueId = generateUniqueId();
        String date = LocalDate.now().format(dateFormatter);
        String shortId = String.format("%06d", uniqueId % 1_000_000);
        return String.format("TRADE-%s-%s-%s", serviceCode, date, shortId);
    }
}