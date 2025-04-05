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
import java.util.Collections;
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
            Pattern.compile("^/(\\d+)(?:/.*)?$"),           // '/1'
            Pattern.compile("^/users/(\\d+)(?:/.*)?$"),     // '/users/1'
            Pattern.compile("^/pet/(\\d+)(?:/.*)?$")        // '/pet/1'
//            Pattern.compile("^/users/(\\d+)(?:/.*)?$"),
//            Pattern.compile("^/pet/(\\d+)(?:/.*)?$")
//            Pattern.compile("/(?:api/)?users/(\\d+)(?:/.*)?"),
//            Pattern.compile("/(?:api/)?pet/(\\d+)(?:/.*)?"),
//            Pattern.compile("/(?:api/)?budget/(\\d+)(?:/.*)?"),
//            Pattern.compile("/(?:api/)?feedback/(\\d+)(?:/.*)?"),
//            Pattern.compile("/(?:api/)?notifications(?:/.*)?"),
//            Pattern.compile("/(?:api/)?products/liked"),
//            Pattern.compile("/(?:api/)?products/(\\d+)/like"),
//            Pattern.compile("/(?:api/)?products/(\\d+)/alert")
    );

    // 쿼리 파라미터 목록
    private final List<String> userIdParamNames = Arrays.asList(
            "userId", "user_id", "user-id", "user_id"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.info("인터셉터 호출: {}", requestURI);

        if (requestURI.startsWith("/test/")) {
            log.info("테스트 경로 인증 우회: {}", requestURI);
            return true;
        }

        if (requestURI.contains("/test/auth/")) {
            log.info("테스트 인증 경로 인증 우회: {}", requestURI);
            return true;
        }

        log.info("요청 헤더:");
        Collections.list(request.getHeaderNames()).forEach(name ->
                log.info("{}: {}", name, request.getHeader(name)));

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
            log.info("RequiresUser 어노테이션 없음: {}", requestURI);
            return true;
        }

        log.info("RequiresUser 어노테이션 발견: {}, ownerOnly={}", requestURI, requiresUser.ownerOnly());

        // 게이트웨이에서 넘어온 사용자 ID 확인
        String userIdHeader = request.getHeader("X-User-Id");

        // ===== 추가 수정 부분 시작 =====
        // 다른 헤더도 확인
        if (userIdHeader == null || userIdHeader.isEmpty()) {
            userIdHeader = request.getHeader("X-Auth-UserId");
        }

        // 헤더가 없다면 Authorization 헤더에서 직접 추출 시도 (선택적 구현)
        if (userIdHeader == null || userIdHeader.isEmpty()) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                try {
                    String token = authHeader.substring(7);
                    log.info("Authorization 헤더에서 사용자 ID 추출 시도: {}", token);
                    // JwtUtil이 주입되어 있다면 아래 주석을 해제하고 사용
                    // userIdHeader = String.valueOf(jwtUtil.getUserIdFromToken(token));
                } catch (Exception e) {
                    log.error("토큰에서 사용자 ID 추출 실패: {}", e.getMessage());
                }
            }
        }
        // ===== 추가 수정 부분 끝 =====

        if (userIdHeader == null || userIdHeader.isEmpty()) {
            log.error("인증 헤더 없음 - 인증 실패: {}", requestURI);
            log.error("모든 요청 헤더:");
            Collections.list(request.getHeaderNames()).forEach(name ->
                    log.error("  {}: {}", name, request.getHeader(name)));
            throw new AccessDeniedException("인증이 필요합니다. 사용자 ID 헤더가 없습니다.");
        }

        Long currentUserId;
        try {
            currentUserId = Long.parseLong(userIdHeader);
            log.info("현재 인증된 사용자 ID: {}", currentUserId);
        } catch (NumberFormatException e) {
            log.error("X-User-Id 헤더가 유효한 숫자가 아님: {}", userIdHeader);
            throw new AccessDeniedException("유효하지 않은 사용자 ID 형식입니다.");
        }

        // 대상 사용자 ID 추출
        Long targetUserId = extractTargetUserId(request);
        log.info("대상 사용자 ID: {}", targetUserId);

        // 소유자 검증: ownerOnly가 true이고 targetUserId가 있으면 검증
        if (requiresUser.ownerOnly() && targetUserId != null && !currentUserId.equals(targetUserId)) {
            log.error("권한 없음: 현재 사용자({})는 대상 사용자({})의 자원에 접근할 수 없습니다.", currentUserId, targetUserId);
            throw new AccessDeniedException("다른 사용자의 정보에 접근할 수 없습니다.");
        }

        log.info("인증/인가 성공: {}, 사용자: {}", requestURI, currentUserId);
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

        log.info("URI 패턴 매칭: {}", uri);

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