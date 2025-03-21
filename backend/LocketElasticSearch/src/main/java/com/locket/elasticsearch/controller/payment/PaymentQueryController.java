package com.locket.elasticsearch.controller.payment;

import com.locket.elasticsearch.payment.entity.PaymentHistory;
import com.locket.elasticsearch.service.payment.PaymentQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentQueryController {

    private final PaymentQueryService paymentQueryService;

    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getMonthlyPaymentHistory(
            @PathVariable int userId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        try {
            List<PaymentHistory> historyList = paymentQueryService.findByUserAndMonth(userId, year, month);
            return ResponseEntity.ok(historyList);
        } catch (IOException e) {
            log.error("❌ Elasticsearch 조회 중 오류 발생", e);
            return ResponseEntity.status(500).body("Elasticsearch 조회 중 오류가 발생했습니다.");
        }
    }
}
