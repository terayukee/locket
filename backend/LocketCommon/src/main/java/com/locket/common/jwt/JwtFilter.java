package com.locket.common.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
//@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final List<Pattern> excludedPatterns = Arrays.asList(
            // 인증이 필요 없는 URL 패턴
            Pattern.compile("^/api/users/login$"),
            Pattern.compile("^/api/users/signup$")
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

//        log.info("Processing JWT authentication for path: {}", request.getRequestURI());

        try {
            // 인증 헤더 가져오기
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("인증이 필요합니다.");
                return;
            }

            // 토큰 추출 (Bearer 제거)
            String token = authHeader.substring(7);

            // 토큰 유효성 검증
            if (!jwtUtil.validateToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("유효하지 않은 토큰입니다.");
                return;
            }

            // 토큰에서 사용자 ID 추출
            Long userId = jwtUtil.getUserIdFromToken(token);

            // 사용자 ID와 URL 경로의 ID 일치 여부 확인 (사용자 정보 접근 시)
            String path = request.getRequestURI();
            if (path.matches("^/api/users/\\d+$")) {
                Long pathUserId = extractUserIdFromPath(path);

                // 요청 경로의 사용자 ID가 토큰의 사용자 ID와 일치하는지 확인
                if (pathUserId != null && !pathUserId.equals(userId)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("다른 사용자의 정보에 접근할 권한이 없습니다.");
                    return;
                }
            }

            // 요청 속성에 사용자 ID 설정 (컨트롤러에서 사용)
            request.setAttribute("userId", userId);

            // 요청 진행
            filterChain.doFilter(request, response);

        } catch (Exception e) {
//            log.error("JWT 인증 처리 중 오류 발생", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(e.getMessage());
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
//        log.info("Checking path against exclude patterns: {}", path);

        return excludedPatterns.stream()
                .anyMatch(pattern -> pattern.matcher(path).matches());
    }

    // URL 경로에서 사용자 ID 추출
    private Long extractUserIdFromPath(String path) {
        try {
            String[] parts = path.split("/");
            if (parts.length > 0) {
                String lastPart = parts[parts.length - 1];
                return Long.parseLong(lastPart);
            }
        } catch (NumberFormatException e) {
//            log.warn("Failed to parse user ID from path: {}", path);
        }
        return null;
    }
}