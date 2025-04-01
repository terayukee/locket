package com.locket.user.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class SwaggerConfig {

    @Value("${swagger.server-url}")  // ✅ yml에서 동적 주입
    private String serverUrl;

    @Bean
    public OpenAPI customOpenAPI() {

        // JWT 보안 스키마 정의
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        // 보안 요구사항
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        Schema integerSchema = new Schema()
                .type("integer")
                .format("int32");

        // 오류 응답
        Schema errorResponseSchema = new Schema()
                .type("object")
                .description("오류 응답")
                .addProperty("status", new Schema().type("integer").example(404))
                .addProperty("error", new Schema().type("string").example("Not Found"))
                .addProperty("message", new Schema().type("string").example("사용자를 찾을 수 없습니다."))
                .addProperty("timestamp", new Schema().type("string").format("date-time").example("2025-03-31T06:33:44.061Z"));

        // 각 HTTP 상태 코드별 표준 응답 정의
        Map<String, ApiResponse> responses = new HashMap<>();

        // 200 OK 응답
        responses.put("200", new ApiResponse()
                .description("성공적으로 처리되었습니다."));

        // 400 Bad Request 응답
        responses.put("400", new ApiResponse()
                .description("잘못된 요청입니다.")
                .content(new Content().addMediaType("application/json",
                        new MediaType().schema(errorResponseSchema))));

        // 401 Unauthorized 응답
        responses.put("401", new ApiResponse()
                .description("인증에 실패했습니다.")
                .content(new Content().addMediaType("application/json",
                        new MediaType().schema(errorResponseSchema))));

        // 404 Not Found 응답
        responses.put("404", new ApiResponse()
                .description("요청한 리소스를 찾을 수 없습니다.")
                .content(new Content().addMediaType("application/json",
                        new MediaType().schema(errorResponseSchema))));

        // 500 Internal Server Error 응답
        responses.put("500", new ApiResponse()
                .description("서버 내부 오류가 발생했습니다.")
                .content(new Content().addMediaType("application/json",
                        new MediaType().schema(errorResponseSchema))));

        Components components = new Components()
                .addSecuritySchemes("bearerAuth", securityScheme)
                .addSchemas("Integer", integerSchema)
                .addSchemas("ErrorResponse", errorResponseSchema);

        // 각 응답 컴포넌트 추가
        for (Map.Entry<String, ApiResponse> entry : responses.entrySet()) {
            components.addResponses(entry.getKey(), entry.getValue());
        }

        return new OpenAPI()
                .components(components)
                .addSecurityItem(securityRequirement)
                .servers(List.of(
                        new io.swagger.v3.oas.models.servers.Server()
                                .url(serverUrl)
                ))
                .info(new Info()
                        .title("Locket User API")
                        .version("1.0")
                        .description("종합 서비스 관련 API 문서"));
    }
}