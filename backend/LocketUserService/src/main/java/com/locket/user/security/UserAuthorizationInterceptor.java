package com.locket.user.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.locket.user.exception.AccessDeniedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserAuthorizationInterceptor implements HandlerInterceptor {

    // ObjectMapper 필드 추가
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 사용자 ID를 포함하는 경로 패턴 목록 (정규식)
    private final List<Pattern> userIdPatterns = Arrays.asList(
            Pattern.compile("/(?:api/)?users/(\\d+)(?:/.*)?"),
            Pattern.compile("/(?:api/)?pet/(\\d+)(?:/.*)?"),
            Pattern.compile("/(?:api/)?budget/(\\d+)(?:/.*)?"),
            Pattern.compile("/(?:api/)?feedback/(\\d+)(?:/.*)?"),
            Pattern.compile("/(?:api/)?notifications(?:/.*)?"),
            Pattern.compile("/(?:api/)?products/liked"),
            Pattern.compile("/(?:api/)?products/(\\d+)/like"),
            Pattern.compile("/(?:api/)?products/(\\d+)/alert")
    );

    // 쿼리 파라미터 목록
    private final List<String> userIdParamNames = Arrays.asList(
            "userId", "user_id", "user-id", "user_id"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 메서드에서 어노테이션 찾기
        RequiresUser requiresUser = handlerMethod.getMethodAnnotation(RequiresUser.class);

        // 메서드에 없으면 클래스에서 찾기
        if (requiresUser == null) {
            requiresUser = handlerMethod.getBeanType().getAnnotation(RequiresUser.class);
        }

        // 어노테이션이 없으면 인가 체크 필요 없음
        if (requiresUser == null) {
            return true;
        }

        // 게이트웨이에서 넘어온 사용자 ID 확인
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader == null) {
            throw new AccessDeniedException("인증이 필요합니다.");
        }

        Long currentUserId = Long.parseLong(userIdHeader);
        log.debug("현재 인증된 사용자 ID: {}", currentUserId);

        // 대상 사용자 ID 추출
        Long targetUserId = extractTargetUserId(request);
        log.debug("대상 사용자 ID: {}", targetUserId);

        // 여기에 사용자 ID 검증 로직 추가 - ownerOnly가 true이고 targetUserId가 있으면 검증
        if (requiresUser.ownerOnly() && targetUserId != null && !currentUserId.equals(targetUserId)) {
            throw new AccessDeniedException("다른 사용자의 정보에 접근할 수 없습니다.");
        }

        return true;
    }

    private Long extractTargetUserId(HttpServletRequest request) {
        // 경로 패턴에서 userId 추출 시도
        Long pathUserId = extractUserIdFromPath(request);
        if (pathUserId != null) {
            return pathUserId;
        }

        // 쿼리 파라미터에서 userId 추출 시도
        Long queryUserId = extractUserIdFromQueryParams(request);
        if (queryUserId != null) {
            return queryUserId;
        }

        // 요청 본문에서 userId 추출 시도
        return extractUserIdFromRequestBody(request);
    }

    private Long extractUserIdFromPath(HttpServletRequest request) {
        String uri = request.getRequestURI();

        // /api/users/(숫자)
        for (Pattern pattern : userIdPatterns) {
            Matcher matcher = pattern.matcher(uri);
            if (matcher.matches() && matcher.groupCount() >= 1) {
                try {
                    return Long.parseLong(matcher.group(1));
                } catch (NumberFormatException e) {
                    log.debug("경로 userId 파싱 실패: {}", e.getMessage());
                }
            }
        }

        return null;
    }

    private boolean isUserResourceIndicator(String segment) {
        // 사용자 ID가 뒤따를 수 있는 리소스 지시자 목록
        List<String> userResourceIndicators = Arrays.asList(
                "users", "user", "pet", "products", "budget", "feedback"
        );
        return userResourceIndicators.contains(segment.toLowerCase());
    }

    private Long extractUserIdFromQueryParams(HttpServletRequest request) {
        for (String paramName : userIdParamNames) {
            String paramValue = request.getParameter(paramName);
            if (paramValue != null && !paramValue.isEmpty()) {
                try {
                    log.debug("쿼리 파라미터 {}에서 userId 추출: {}", paramName, paramValue);
                    return Long.parseLong(paramValue);
                } catch (NumberFormatException e) {
                    log.debug("쿼리 파라미터 {}의 값({})이 숫자 변환에 실패했습니다.", paramName, paramValue);
                }
            }
        }

        return null;
    }

    private Long extractUserIdFromRequestBody(HttpServletRequest request) {
        try {
            if (request.getContentType() != null && request.getContentType().contains("application/json")) {
                // 요청 본문 읽기 (ContentCachingRequestWrapper를 통해 캐싱된 본문)
                String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

                if (body == null || body.isEmpty()) {
                    return null;
                }

                // JSON 파싱
                JsonNode rootNode = objectMapper.readTree(body);

                // userId 필드 검색 (다양한 형태의 userId 필드명 처리)
                if (rootNode.has("userId")) {
                    return rootNode.get("userId").asLong();
                } else if (rootNode.has("user_id")) {
                    return rootNode.get("user_id").asLong();
                } else if (rootNode.has("user-id")) {
                    return rootNode.get("user-id").asLong();
                } else if (rootNode.has("userID")) {
                    return rootNode.get("userID").asLong();
                } else if (rootNode.has("user")) {
                    JsonNode user = rootNode.get("user");
                    if (user.isObject() && user.has("id")) {
                        return user.get("id").asLong();
                    }
                }
            }
        } catch (Exception e) {
            log.debug("요청 본문에서 userId 추출 실패: {}", e.getMessage());
        }
        return null;
    }
}