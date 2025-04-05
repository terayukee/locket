package com.locket.user.feign;

import com.locket.payment.dto.PaymentHistoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

//@FeignClient(name = "elasticsearch-service", contextId = "paymentHistoryClient")
@FeignClient(
        name = "elasticsearch-service",
        contextId = "paymentHistoryClient",
        url = "http://172.26.5.222:8083" // 실제 컨테이너 IP 및 포트
)
public interface PaymentHistoryFeignClient {

    @GetMapping("/payment/history")
    List<PaymentHistoryDto> getPaymentHistories(
            @RequestParam("userId") long userId,
            @RequestParam("year") int year,
            @RequestParam("month") int month
    );
}
