package com.locket.user.controller.payment;

import com.locket.kafka.event.PaymentSuccessEvent;
import com.locket.user.service.payment.elastic.ElasticSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/payment/history")
@RequiredArgsConstructor
public class PaymentHistoryController {

    private final ElasticSearchService elasticSearchService;

    @GetMapping("/{transactionId}")
    public Optional<PaymentSuccessEvent> getPaymentHistory(@PathVariable String transactionId) {
        return elasticSearchService.getPaymentEventById(transactionId);
    }
}
