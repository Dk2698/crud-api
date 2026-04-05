package com.kumar.crudapi.trace;

import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TradeService {

    private static final Logger log = LoggerFactory.getLogger(TradeService.class);
    private final TradeIdGenerator tradeIdGenerator;
    private final Tracer tracer;

    public TradeService(TradeIdGenerator tradeIdGenerator, Tracer tracer) {
        this.tradeIdGenerator = tradeIdGenerator;
        this.tracer = tracer;
    }

    public void processTrade(String serviceCode) {
        String tradeId = tradeIdGenerator.generateTradeId(serviceCode);

        // Fetch traceId and spanId from Micrometer Tracing
        String traceId = tracer.currentSpan().context().traceId();
        String spanId = tracer.currentSpan().context().spanId();

        log.info("Processing trade {} (traceId={}, spanId={})", tradeId, traceId, spanId);

        // ... business logic here ...

        log.info("Trade {} processed successfully", tradeId);
    }
}