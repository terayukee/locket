package com.locket.payment.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

/**
 * HTTP 요청 본문을 캐싱하는 필터
 * 요청 본문을 여러 번 읽을 수 있도록 ContentCachingRequestWrapper로 감싸서 전달
 * 인증/인가 처리에서 요청 본문의 사용자 ID를 추출
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestBodyCachingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getMethod().equals("POST") ||
                request.getMethod().equals("PUT") ||
                request.getMethod().equals("PATCH")) {

            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
            filterChain.doFilter(wrappedRequest, response);
        } else {

            filterChain.doFilter(request, response);
        }
    }
}