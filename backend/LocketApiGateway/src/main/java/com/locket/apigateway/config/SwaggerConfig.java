package com.locket.apigateway.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {
    private final RouteDefinitionLocator locator;

    public SwaggerConfig(RouteDefinitionLocator locator) {
        this.locator = locator;
    }

    @Bean
    public List<GroupedOpenApi> apis() {
        List<GroupedOpenApi> groups = new ArrayList<>();
        List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();

        if (definitions != null) {
            // 서비스 ID 기반으로 API 그룹 생성
            definitions.stream()
                    .filter(routeDefinition -> routeDefinition.getId() != null && routeDefinition.getId().endsWith("-service"))
                    .forEach(routeDefinition -> {
                        String serviceId = routeDefinition.getId();

                        // 서비스 ID에서 '-service' 제거
                        String serviceName = serviceId.replace("-service", "");

                        // 첫 글자 대문자로 변환
                        String formattedName = serviceName.substring(0, 1).toUpperCase() + serviceName.substring(1);

                        // 라우트 ID에 따른 경로 패턴 설정
                        String pathPattern = switch (serviceId) {
                            case "user-service" -> "/api/users/**";
                            case "payment-service" -> "/api/payment/**";
                            case "elasticsearch-service" -> "/api/elasticsearch/**";
                            case "receipt-service" -> "/api/receipts/**";
                            default -> "/**";
                        };

                        // GroupedOpenApi 생성 및 추가
                        groups.add(GroupedOpenApi.builder()
                                .pathsToMatch(pathPattern)
                                .group("Locket " + formattedName + " Service")
                                .build());
                    });

            // 기본 API 그룹 추가 (위 케이스에 포함되지 않는 API들을 위해)
            if (groups.isEmpty()) {
                groups.add(GroupedOpenApi.builder()
                        .group("Default")
                        .pathsToMatch("/**")
                        .build());
            }
        }

        return groups;
    }
}
