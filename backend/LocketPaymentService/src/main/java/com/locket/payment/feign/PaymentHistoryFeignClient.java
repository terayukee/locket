package com.locket.payment.feign;

import com.locket.payment.domain.pay.dto.CardMonthlyUsageDto;
import com.locket.payment.domain.pay.dto.MonthPaymentDto;
import com.locket.payment.dto.PaymentHistoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

//@FeignClient(name = "elasticsearch-service", contextId = "paymentHistoryClient")
@FeignClient(
        name = "elasticsearch-service",
        contextId = "paymentHistoryClient",
        url = "${feign.client.elasticsearch-service.url}" // 실제 컨테이너 IP 및 포트
)
public interface PaymentHistoryFeignClient {

    // ✅ 카드 기준 월간 총 결제 금액
    @GetMapping("/payment/history/card-total")
    CardMonthlyUsageDto getMonthlyTotalAmountByCard(
            @RequestParam("userId") long userId,
            @RequestParam("cardId") int cardId,
            @RequestParam("year") int year,
            @RequestParam("month") int month
    );

    @GetMapping("/payment/month")
    List<MonthPaymentDto> getMonthPayments(
            @RequestParam("userId") long userId,
            @RequestParam("year") int year,
            @RequestParam("month") int month
    );

}
