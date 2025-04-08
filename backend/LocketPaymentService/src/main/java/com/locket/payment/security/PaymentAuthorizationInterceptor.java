package com.locket.payment.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class PaymentAuthorizationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.debug("📌 요청 URI: {}", requestURI);

        // Handler가 메서드가 아니면 스킵
        if (!(handler instanceof HandlerMethod)) return true;

        HandlerMethod method = (HandlerMethod) handler;

        // @RequiresUser 어노테이션 확인
        RequiresUser requiresUser = method.getMethodAnnotation(RequiresUser.class);
        if (requiresUser == null) {
            requiresUser = method.getBeanType().getAnnotation(RequiresUser.class);
        }

        if (requiresUser == null) return true;

        // 인증된 사용자 ID 파싱
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

        // ownerOnly == true 이면 실제 userId 비교
        if (requiresUser.ownerOnly()) {
            String targetUserParam = request.getParameter("userId");

            if (targetUserParam != null && !targetUserParam.isBlank()) {
                try {
                    Long targetUserId = Long.parseLong(targetUserParam);
                    log.debug("🎯 요청 대상 사용자 ID: {}", targetUserId);

                    if (!currentUserId.equals(targetUserId)) {
                        log.error("⚠️ 권한 없음: 현재 사용자({}) != 대상 사용자({})", currentUserId, targetUserId);
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "다른 사용자의 정보에 접근할 수 없습니다.");
                        return false;
                    }

                } catch (NumberFormatException e) {
                    log.warn("❌ userId 파라미터 파싱 실패: {}", targetUserParam);
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "userId 파라미터 형식이 잘못되었습니다.");
                    return false;
                }
            }
        }

        return true;
    }
}
