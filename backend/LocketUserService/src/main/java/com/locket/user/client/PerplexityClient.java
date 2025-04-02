package com.locket.user.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PerplexityClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    private static final String API_URL = "https://api.perplexity.ai/chat/completions";
    private static final String API_KEY = "YOUR_API_KEY";  // TODO: 보안 처리

    public String summarizeFeedback(List<String> insights, List<String> recommendations) {
        try {
            String joined = String.join("\n", insights) + "\n\n" + String.join("\n", recommendations);
            String prompt = "다음은 소비 패턴 분석과 절약 제안입니다. 사용자가 쉽게 이해할 수 있도록 간결하고 핵심적인 요약문을 생성해 주세요:\n\n" + joined;

            String requestBody = objectMapper.writeValueAsString(
                    Map.of(
                            "model", "llama-3-sonar-small-32k-online",
                            "messages", List.of(Map.of("role", "user", "content", prompt))
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(API_KEY);

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, request, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("choices").get(0).path("message").path("content").asText();

        } catch (Exception e) {
            log.error("🛑 퍼플렉시티 요약 실패: {}", e.getMessage(), e);
            return "요약 생성 중 오류가 발생했습니다.";
        }
    }
}
