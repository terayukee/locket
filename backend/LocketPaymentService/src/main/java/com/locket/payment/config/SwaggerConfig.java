package com.locket.payment.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class SwaggerConfig {

    @Value("${swagger.server-url}")
    private String serverUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        // 🔐 JWT 인증 스키마 설정
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        // 📦 공통 integer 스키마
        Schema<?> integerSchema = new Schema<>()
                .type("integer")
                .format("int32");

        // 📦 오류 응답 스키마
        Schema<?> errorResponseSchema = new Schema<>()
                .type("object")
                .description("오류 응답")
                .addProperty("status", new Schema<>().type("integer").example(400))
                .addProperty("error", new Schema<>().type("string").example("Bad Request"))
                .addProperty("message", new Schema<>().type("string").example("요청이 잘못되었습니다."))
                .addProperty("timestamp", new Schema<>().type("string").format("date-time").example("2025-04-01T12:34:56.789Z"));

        // 📦 표준 오류 응답 정의
        Map<String, ApiResponse> responses = new HashMap<>();

        responses.put("400", new ApiResponse()
                .description("잘못된 요청입니다.")
                .content(new Content().addMediaType("application/json", new MediaType().schema(errorResponseSchema))));

        responses.put("401", new ApiResponse()
                .description("인증에 실패했습니다.")
                .content(new Content().addMediaType("application/json", new MediaType().schema(errorResponseSchema))));

        responses.put("404", new ApiResponse()
                .description("요청한 리소스를 찾을 수 없습니다.")
                .content(new Content().addMediaType("application/json", new MediaType().schema(errorResponseSchema))));

        responses.put("500", new ApiResponse()
                .description("서버 내부 오류가 발생했습니다.")
                .content(new Content().addMediaType("application/json", new MediaType().schema(errorResponseSchema))));

        // 📦 컴포넌트 설정
        Components components = new Components()
                .addSecuritySchemes("bearerAuth", securityScheme)
                .addSchemas("Integer", integerSchema)
                .addSchemas("ErrorResponse", errorResponseSchema);

        for (Map.Entry<String, ApiResponse> entry : responses.entrySet()) {
            components.addResponses(entry.getKey(), entry.getValue());
        }

        return new OpenAPI()
                .components(components)
                .addSecurityItem(securityRequirement)
                .servers(List.of(new Server().url(serverUrl)))
                .info(new Info()
                        .title("Locket Payment API")
                        .version("1.0")
                        .description("Locket 결제 서비스 관련 API 문서"));
    }
}
