package com.kumar.crudapi.trace;

import org.springframework.context.annotation.Configuration;

@Configuration
public class TracingConfig {

//    @Bean
//    public Tracer tracer() {
//        // Configure Jaeger exporter
//        JaegerGrpcSpanExporter jaegerExporter = JaegerGrpcSpanExporter.builder()
//                .setEndpoint("http://localhost:14250")
//                .build();
//
//        // Set up SDK tracer provider with batch span processor
//        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
//                .addSpanProcessor(BatchSpanProcessor.builder(jaegerExporter).build())
//                .build();
//
//        // Register global OpenTelemetry
//        GlobalOpenTelemetry.set(GlobalOpenTelemetry.builder()
//                .setTracerProvider(sdkTracerProvider)
//                .build()
//        );
//
//        // Micrometer Tracer
//        return new OtelTracer(GlobalOpenTelemetry.get().getTracer("fintech-service"));
//    }
}
