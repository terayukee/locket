package com.locket.apigateway.filter;

import com.locket.common.jwt.JwtUtil;
import com.locket.common.jwt.JwtUtil.TokenStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class JwtGatewayFilter extends AbstractGatewayFilterFactory<JwtGatewayFilter.Config> {

    private final JwtUtil jwtUtil;

    private final List<Pattern> excludedPatterns = Arrays.asList(
            // 인증이 필요 없는 URL 패턴
            Pattern.compile("^/api/users/login$"),
            Pattern.compile("^/api/users/signup$"),
            Pattern.compile("^/api/users/refresh$"),
            Pattern.compile("^/swagger-ui.html$"),
            Pattern.compile("^/swagger-ui/.*$"),
            Pattern.compile("^/v3/api-docs/.*$"),
            Pattern.compile("^/v3/api-docs$"),
            Pattern.compile("^/webjars/.*$")
    );

    // 사용자 관련 API 패턴
    private final List<Pattern> userApiPatterns = Arrays.asList(
            Pattern.compile("^/api/users/(\\d+)(?:/.*)?$"),       // 사용자 리소스
            Pattern.compile("^/api/user-profiles/(\\d+)(?:/.*)?$"), // 사용자 프로필
            Pattern.compile("^/api/accounts/(\\d+)(?:/.*)?$")     // 사용자 계정
    );

    public JwtGatewayFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    public static class Config {

    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            log.info("Processing JWT authentication for path: {}", path);

            // 인증 제외 경로 확인
            if (isExcludedPath(path)) {
                return chain.filter(exchange);
            }

            // 인증 헤더 확인
            List<String> authHeaders = request.getHeaders().getOrEmpty("Authorization");
            if (authHeaders.isEmpty() || !authHeaders.get(0).startsWith("Bearer ")) {
                return onError(exchange, "인증이 필요합니다.", HttpStatus.UNAUTHORIZED);
            }

            // 토큰 추출 (Bearer 제거)
            String token = authHeaders.get(0).substring(7);

            try {
                // 토큰 유효성 검증 (개선된 방식)
                TokenStatus tokenStatus = jwtUtil.validateTokenWithStatus(token);

                if (tokenStatus != TokenStatus.VALID) {
                    if (tokenStatus == TokenStatus.EXPIRED) {
                        return onError(exchange, "만료된 토큰입니다. 토큰을 갱신해주세요.", HttpStatus.UNAUTHORIZED);
                    } else {
                        return onError(exchange, "유효하지 않은 토큰입니다: " + tokenStatus, HttpStatus.UNAUTHORIZED);
                    }
                }

                // 액세스 토큰 타입 확인
                if (!jwtUtil.isAccessToken(token)) {
                    return onError(exchange, "유효한 액세스 토큰이 아닙니다.", HttpStatus.UNAUTHORIZED);
                }

                // 토큰에서 사용자 ID 추출
                Long userId = jwtUtil.getUserIdFromToken(token);

                // 사용자 ID와 URL 경로의 ID 일치 여부 확인 (사용자 리소스 접근 시)
                Long pathUserId = extractUserIdFromPath(path);
                if (pathUserId != null && !pathUserId.equals(userId)) {
                    return onError(exchange, "다른 사용자의 정보에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN);
                }

                // 사용자 ID를 헤더에 추가
                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-User-Id", String.valueOf(userId))
                        .build();

                // 변경된 요청으로 교체
                return chain.filter(exchange.mutate().request(mutatedRequest).build());

            } catch (Exception e) {
                log.error("JWT 인증 처리 중 오류 발생", e);
                return onError(exchange, e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private boolean isExcludedPath(String path) {
        return excludedPatterns.stream()
                .anyMatch(pattern -> pattern.matcher(path).matches());
    }

    // 사용자 ID 추출
    private Long extractUserIdFromPath(String path) {
        // 사용자 관련 API 패턴만 처리
        for (Pattern pattern : userApiPatterns) {
            Matcher matcher = pattern.matcher(path);
            if (matcher.matches()) {
                try {
                    return Long.parseLong(matcher.group(1));
                } catch (NumberFormatException e) {
                    log.warn("Failed to parse user ID from path: {} - {}", path, e.getMessage());
                }
            }
        }
        return null;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);

        return response.writeWith(Mono.just(
                response.bufferFactory().wrap(message.getBytes())
        ));
    }
}