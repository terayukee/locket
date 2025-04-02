package com.locket.user.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.locket.user.domain.feedback.dto.PerplexitySummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${PERPLEXITY.API_KEY}")  // 🔐 Jenkins 또는 yml에서 주입
    private String perplexityApiKey;

    private static final String API_URL = "https://api.perplexity.ai/chat/completions";

    public PerplexitySummaryResponse summarizeFeedback(String insights, String recommendations) {
        try {
            String prompt = """
            아래는 사용자 소비 분석 인사이트와 절약 제안입니다.
            아래 내용을 요약하여 다음 JSON 형식으로만 응답해 주세요. 다른 텍스트는 포함하지 마세요:

            {
              "insights": "여기에 소비 분석 요약",
              "recommendations": "여기에 절약 제안 요약"
            }

            ## 소비 분석 인사이트
            %s

            ## 절약 제안
            %s
            """.formatted(insights, recommendations);

            String requestBody = objectMapper.writeValueAsString(
                    Map.of(
                            "model", "sonar",
                            "messages", List.of(Map.of("role", "user", "content", prompt))
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(perplexityApiKey);

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, request, String.class);

            // ✅ 응답 파싱: content 필드에서 JSON 파싱
            JsonNode contentNode = objectMapper.readTree(response.getBody())
                    .path("choices").get(0).path("message").path("content");

            String jsonText = contentNode.asText().trim();

            // 👉 마크다운 코드블록 제거
            if (jsonText.startsWith("```json")) {
                jsonText = jsonText.replaceFirst("(?s)```json\\s*", "").replaceFirst("```\\s*$", "").trim();
            } else if (jsonText.startsWith("```")) {
                jsonText = jsonText.replaceFirst("(?s)```\\s*", "").replaceFirst("```\\s*$", "").trim();
            }

            if (!jsonText.startsWith("{")) {
                log.warn("⚠ 퍼플렉시티 응답이 JSON이 아님: {}", jsonText);
                throw new IllegalStateException("퍼플렉시티 응답이 JSON 형식이 아닙니다.");
            }

            return objectMapper.readValue(jsonText, PerplexitySummaryResponse.class);

        } catch (Exception e) {
            log.error("🛑 퍼플렉시티 요약 실패: {}", e.getMessage(), e);
            return PerplexitySummaryResponse.builder()
                    .insights("요약 중 오류가 발생했습니다.")
                    .recommendations("요약 중 오류가 발생했습니다.")
                    .build();
        }
    }
}
