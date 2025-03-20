package com.locket.elasticsearch.config;

import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ElasticSearchSettings {

    @PostConstruct
    public void initIndexSettings() {
        Map<String, Object> settings = new HashMap<>();
        settings.put("index.mapping.total_fields.limit", 2000); // 🔥 필요한 설정 추가 가능

        // ⚠️ 실제 Elasticsearch 인덱스 설정 변경은 REST API로 수행해야 함
    }
}
