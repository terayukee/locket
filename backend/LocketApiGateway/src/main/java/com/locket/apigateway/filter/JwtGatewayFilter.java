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

    // 인증이 필요 없는 URL 패턴들 (그대로 유지)
    private final List<Pattern> excludedPatterns = Arrays.asList(
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
            Pattern.compile("^/api/users/(\\d+)(?:/.*)?$"),
            Pattern.compile("^/api/user-profiles/(\\d+)(?:/.*)?$"),
            Pattern.compile("^/api/accounts/(\\d+)(?:/.*)?$"),
            Pattern.compile("^/api/users/pet(?:\\?.*)?$"),
            Pattern.compile("^/api/users/test/auth/(\\d+)$"),
            Pattern.compile("^/api/budget(?:/.*)?$"),
            Pattern.compile("^/api/feedback/(\\d+)(?:/.*)?$"),
            Pattern.compile("^/api/notifications(?:/.*)?$"),
            Pattern.compile("^/api/products/liked(?:\\?.*userId=(\\d+))?$"),
            Pattern.compile("^/api/products/(\\d+)/like$")
    );

    public JwtGatewayFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    public static class Config {}

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            log.info("Processing JWT authentication for path: {}", path);

            if (isExcludedPath(path)) {
                return chain.filter(exchange);
            }

            // Authorization 헤더 검사
            List<String> authHeaders = request.getHeaders().getOrEmpty("Authorization");
            if (authHeaders.isEmpty() || !authHeaders.get(0).startsWith("Bearer ")) {
                return onError(exchange, "인증이 필요합니다.", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeaders.get(0).substring(7);

            try {
                // JWT 유효성 검사
                TokenStatus tokenStatus = jwtUtil.validateTokenWithStatus(token);
                if (tokenStatus != TokenStatus.VALID) {
                    if (tokenStatus == TokenStatus.EXPIRED) {
                        return onError(exchange, "만료된 토큰입니다. 토큰을 갱신해주세요.", HttpStatus.UNAUTHORIZED);
                    } else {
                        return onError(exchange, "유효하지 않은 토큰입니다: " + tokenStatus, HttpStatus.UNAUTHORIZED);
                    }
                }

                if (!jwtUtil.isAccessToken(token)) {
                    return onError(exchange, "유효한 액세스 토큰이 아닙니다.", HttpStatus.UNAUTHORIZED);
                }

                // 토큰에서 userId 추출
                Long userId = jwtUtil.getUserIdFromToken(token);
                // 경로에서 userId 추출 시도 (우리는 지금 path에는 {userId}가 없으므로 보통 null)
                Long pathUserId = extractUserIdFromPath(path);

                // 쿼리 파라미터에서 userId 추출
                if (pathUserId == null) {
                    pathUserId = extractUserIdFromQueryParam(request);
                }

                // 토큰 userId와 path/쿼리 userId가 다르면 403
                if (pathUserId != null && !pathUserId.equals(userId)) {
                    return onError(exchange, "다른 사용자의 정보에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN);
                }

                // X-User-Id 헤더에 userId를 넣어 내부 서비스로 전달
                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-User-Id", String.valueOf(userId))
                        .build();

                return chain.filter(exchange.mutate().request(mutatedRequest).build());

            } catch (Exception e) {
                log.error("JWT 인증 처리 중 오류 발생", e);
                return onError(exchange, e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private boolean isExcludedPath(String path) {
        return excludedPatterns.stream().anyMatch(pattern -> pattern.matcher(path).matches());
    }

    private Long extractUserIdFromPath(String path) {

        for (Pattern pattern : userApiPatterns) {
            Matcher matcher = pattern.matcher(path);
            if (matcher.matches()) {
                try {
                    return Long.parseLong(matcher.group(1));
                } catch (Exception e) {
                    log.warn("Failed to parse user ID from path: {} - {}", path, e.getMessage());
                }
            }
        }
        return null;
    }

    private Long extractUserIdFromQueryParam(ServerHttpRequest request) {
        // 쿼리 파라미터에서 userId 추출
        String userIdParam = request.getQueryParams().getFirst("userId");
        if (userIdParam != null) {
            try {
                return Long.parseLong(userIdParam);
            } catch (NumberFormatException e) {
                log.warn("Failed to parse user ID from query param: {}", userIdParam);
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
