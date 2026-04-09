package com.kumar.crudapi.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class JacksonConfig {

//    @Bean
//    public JsonMapperBuilderCustomizer jsonCustomizer() {
//        return builder ->
//                builder.(JsonInclude.Include.NON_NULL);

    /// /                        .propertyNamingStrategy(PropertyNamingStrategy.pr)
//    }
//    @Bean
//    public JsonMapperBuilderCustomizer jsonMapperBuilderCustomizer() {
//        return builder -> builder.(Boolean.class, new YesNoToBooleanDeserializer());
//    }

//    @Bean
//    public ObjectMapper objectMapper() {
//        return new ObjectMapper();
//    }
}