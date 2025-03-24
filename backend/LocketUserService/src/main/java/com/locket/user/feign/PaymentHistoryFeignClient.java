package com.locket.user.feign;

import com.locket.user.domain.payment.dto.PaymentHistoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "elasticsearch-service")
public interface PaymentHistoryFeignClient {

    @GetMapping("/api/elasticsearch/payments")
    List<PaymentHistoryDto> getPaymentHistories(@RequestParam("userId") int userId);
}
