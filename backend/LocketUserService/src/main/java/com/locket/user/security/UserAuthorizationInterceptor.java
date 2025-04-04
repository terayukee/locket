package com.locket.user.security;

import com.locket.user.exception.AccessDeniedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserAuthorizationInterceptor implements HandlerInterceptor {

    // 사용자 ID를 포함하는 경로 패턴 목록 (정규식)
    private final List<Pattern> userIdPatterns = Arrays.asList(
            // /users/{userId} 또는 /api/users/{userId} 패턴
            Pattern.compile("/(?:api/)?users/(\\d+)(?:/.*)?"),
            // /pet/{userId} 패턴
            Pattern.compile("/pet/(\\d+)(?:/.*)?")
//            // /products/{userId}/recommendation 패턴
//            Pattern.compile("/products/(\\d+)/recommendation"),
//            // /budget/feedback/{userId} 패턴
//            Pattern.compile("/budget/feedback/(\\d+)"),
//            // /elasticsearch/payment/available/{userId} 패턴
//            Pattern.compile("/elasticsearch/payment/available/(\\d+)")
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

        // ownerOnly 설정이 true이고 현재 사용자와 대상 사용자가 다르면 접근 거부
        if (requiresUser.ownerOnly() && targetUserId != null && !targetUserId.equals(currentUserId)) {
            log.warn("권한 없음: 현재 사용자 {}가 대상 사용자 {}의 리소스에 접근 시도", currentUserId, targetUserId);
            throw new AccessDeniedException("다른 사용자의 자원에 접근할 권한이 없습니다.");
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

        return null;
    }

    private Long extractUserIdFromPath(HttpServletRequest request) {
        String uri = request.getRequestURI();

        // 경로에서 userId 추출
        for (Pattern pattern : userIdPatterns) {
            Matcher matcher = pattern.matcher(uri);
            if (matcher.matches() && matcher.groupCount() >= 1) {
                try {
                    String userIdStr = matcher.group(1);
                    log.debug("URI 경로에서 userId 추출: {}, 패턴: {}", userIdStr, pattern.pattern());
                    return Long.parseLong(userIdStr);
                } catch (NumberFormatException e) {
                    log.debug("경로에서 추출한 userId 값이 숫자 변환에 실패했습니다: {}", e.getMessage());
                }
            }
        }

        // 패턴 탐색 (fallback)
        String[] segments = uri.split("/");
        for (int i = 0; i < segments.length; i++) {

            // 숫자만 있는 세그먼트를 발견하면 확인
            if (segments[i].matches("\\d+")) {

                // 이전 세그먼트 resource 확인
                if (i > 0 && isUserResourceIndicator(segments[i-1])) {
                    try {
                        log.debug("기본 방식으로 URI 경로에서 userId 추출: {}", segments[i]);
                        return Long.parseLong(segments[i]);
                    } catch (NumberFormatException e) {
                        log.debug("경로에서 userId로 의심되는 값 변환 실패: {}", e.getMessage());
                    }
                }
            }
        }

        return null;
    }

    private boolean isUserResourceIndicator(String segment) {
        // 사용자 ID가 뒤따를 수 있는 리소스 지시자 목록
        List<String> userResourceIndicators = Arrays.asList(
                "users", "user", "pet"
//                , "products", "budget", "feedback"
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
}