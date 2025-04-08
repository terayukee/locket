package com.locket.payment.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class PaymentAuthorizationInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final List<Pattern> userIdParamPatterns = Arrays.asList(
            Pattern.compile("^/api/payment/cards"),
            Pattern.compile("^/api/payment/auth-info/fingerprint"),
            Pattern.compile("^/api/payment/monthly-total"),
            Pattern.compile("^/api/payment/auth/verify-password"),
            Pattern.compile("^/api/payment/nfc"),
            Pattern.compile("^/api/payment/validate-card")
    );

    private final List<String> userIdParamNames = Arrays.asList(
            "userId", "user_id", "user-id", "userID"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.debug("📌 요청 URI: {}", requestURI);

        if (!(handler instanceof HandlerMethod)) return true;

        HandlerMethod method = (HandlerMethod) handler;
        RequiresUser requiresUser = method.getMethodAnnotation(RequiresUser.class);
        if (requiresUser == null) {
            requiresUser = method.getBeanType().getAnnotation(RequiresUser.class);
        }
        if (requiresUser == null) return true;

        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader == null || userIdHeader.isBlank()) {
            log.warn("❌ 인증 실패: X-User-Id 헤더 없음");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증이 필요합니다.");
            return false;
        }

        Long currentUserId;
        try {
            currentUserId = Long.parseLong(userIdHeader);
            log.debug("✅ 인증된 사용자 ID: {}", currentUserId);
        } catch (NumberFormatException e) {
            log.error("❌ X-User-Id 파싱 실패: {}", userIdHeader);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "잘못된 사용자 ID 형식");
            return false;
        }

        if (requiresUser.ownerOnly()) {
            Long targetUserId = extractTargetUserId(request);
            if (targetUserId != null && !currentUserId.equals(targetUserId)) {
                log.error("⚠️ 권한 없음: 현재 사용자({}) != 대상 사용자({})", currentUserId, targetUserId);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "다른 사용자의 정보에 접근할 수 없습니다.");
                return false;
            }
        }

        return true;
    }

    private Long extractTargetUserId(HttpServletRequest request) {
        Long queryUserId = extractUserIdFromQueryParams(request);
        if (queryUserId != null) return queryUserId;
        return extractUserIdFromRequestBody(request);
    }

    private Long extractUserIdFromQueryParams(HttpServletRequest request) {
        for (String param : userIdParamNames) {
            String val = request.getParameter(param);
            if (val != null && !val.isBlank()) {
                try {
                    log.debug("쿼리 파라미터 {}에서 userId 추출: {}", param, val);
                    return Long.parseLong(val);
                } catch (NumberFormatException e) {
                    log.warn("❌ userId 파라미터 파싱 실패: {}", val);
                }
            }
        }
        return null;
    }

    private Long extractUserIdFromRequestBody(HttpServletRequest request) {
        try {
            if (request.getContentType() != null && request.getContentType().contains("application/json")) {
                String body = null;
                if (request instanceof ContentCachingRequestWrapper wrapper) {
                    body = new String(wrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
                }

                if (body == null || body.isBlank()) return null;

                JsonNode root = objectMapper.readTree(body);
                for (String field : userIdParamNames) {
                    if (root.has(field)) {
                        return root.get(field).asLong();
                    }
                }
                if (root.has("user") && root.get("user").has("id")) {
                    return root.get("user").get("id").asLong();
                }
            }
        } catch (Exception e) {
            log.error("요청 본문에서 userId 추출 실패: {}", e.getMessage());
        }
        return null;
    }
}
