package com.locket.elasticsearch.controller.payment;

import com.locket.elasticsearch.payment.entity.PaymentHistory;
import com.locket.elasticsearch.service.payment.PaymentQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/elasticsearch/payment")
@RequiredArgsConstructor
public class PaymentQueryController {

    private final PaymentQueryService paymentQueryService;

    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getMonthlyPaymentHistory(
            @PathVariable int userId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        List<PaymentHistory> historyList = paymentQueryService.findByUserAndMonth(userId, year, month);
        return ResponseEntity.ok(historyList);
    }
}
