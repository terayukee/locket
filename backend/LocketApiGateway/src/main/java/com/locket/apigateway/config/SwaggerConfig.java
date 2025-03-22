package com.locket.apigateway.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi paymentServiceApi() {
        return GroupedOpenApi.builder()
                .group("Locket Payment Service")
                .pathsToMatch("/api/payment/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userServiceApi() {
        return GroupedOpenApi.builder()
                .group("Locket User Service")
                .pathsToMatch("/api/user/**")
                .build();
    }

    @Bean
    public GroupedOpenApi elasticSearchApi() {
        return GroupedOpenApi.builder()
                .group("Locket User Service")
                .pathsToMatch("/api/elasticsearch/**")
                .build();
    }
}
