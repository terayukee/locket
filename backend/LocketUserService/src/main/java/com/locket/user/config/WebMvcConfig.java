package com.locket.user.config;

import com.locket.user.security.UserAuthorizationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final UserAuthorizationInterceptor userAuthorizationInterceptor;
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
        registry.addInterceptor(userAuthorizationInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login", "/signup", "/refresh", "/test/**", "/v3/api-docs/**", "/swagger-ui/**");
//                .addPathPatterns("/api/users/**")
//                .addPathPatterns("/pet/**")
//                .addPathPatterns("/budget/**")
//                .addPathPatterns("/notifications/**")
//                .addPathPatterns("/products/liked")
//                .addPathPatterns("/products/*/like")
//                .addPathPatterns("/products/*/alert");

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