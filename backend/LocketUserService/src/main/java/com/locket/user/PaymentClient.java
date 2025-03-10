package com.locket.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "payment-service") // Eureka에서 서비스 이름으로 찾음
public interface PaymentClient {
    @GetMapping("/api/payment/test")
    String getPaymentStatus();
}
