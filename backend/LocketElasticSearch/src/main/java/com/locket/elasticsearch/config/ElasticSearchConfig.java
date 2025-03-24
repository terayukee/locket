package com.locket.elasticsearch.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.locket.elasticsearch.payment.repository")
public class ElasticSearchConfig {
    // ✅ 별도의 클라이언트 설정은 필요하지 않음!
    // Spring Boot가 application.yml을 기반으로 자동 구성함
}
