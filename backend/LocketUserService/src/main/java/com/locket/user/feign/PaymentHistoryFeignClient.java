package com.locket.user.feign;

import com.locket.user.domain.payment.dto.PaymentHistoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "elasticsearch-service", contextId = "paymentHistoryClient")
public interface PaymentHistoryFeignClient {

    @GetMapping("/payment/history")
    List<PaymentHistoryDto> getPaymentHistories(
            @RequestParam("userId") long userId,
            @RequestParam("year") int year,
            @RequestParam("month") int month
    );
}
