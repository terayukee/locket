package com.locket.payment.config;

import com.locket.payment.security.PaymentAuthorizationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final PaymentAuthorizationInterceptor interceptor;
    private final RequestBodyCachingFilter requestBodyCachingFilter;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://j12d204.p.ssafy.io", "http://localhost:8080")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "X-User-Id", "Auth-User-Id")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .addPathPatterns("/**") // 모든 경로에 적용
                .excludePathPatterns(
                        "/swagger-ui/**", "/v3/api-docs/**", "/actuator/**"
                );
    }

    @Bean
    public FilterRegistrationBean<RequestBodyCachingFilter> requestBodyCachingFilterRegistration() {
        FilterRegistrationBean<RequestBodyCachingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(requestBodyCachingFilter);
        registration.addUrlPatterns("/*");
        registration.setName("requestBodyCachingFilter");
        registration.setOrder(1); // 가장 높은 우선순위
        return registration;
    }
}

