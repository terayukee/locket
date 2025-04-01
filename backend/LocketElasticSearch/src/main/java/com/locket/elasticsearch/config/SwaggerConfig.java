package com.locket.elasticsearch.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("https://j12d204.p.ssafy.io/api/elasticsearch")
                ))
                .info(new Info()
                        .title("Locket ElasticSearch API")
                        .version("1.0")
                        .description("ElasticSearch 관련 API 문서"));
    }
}