package com.locket.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.env.Environment;

@Configuration
public class WebClientConfig {

    private final Environment env;

    public WebClientConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public WebClient feedbackWebClient() {
        return WebClient.builder()
                .baseUrl(env.getProperty("locket.spending-pattern.service.url", "http://localhost:8000"))
                .build();
    }
}