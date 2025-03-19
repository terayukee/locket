package com.locket.user.service.payment;

import com.locket.kafka.event.PaymentSuccessEvent;
import com.locket.user.service.payment.elastic.ElasticSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProcessingService {

    private final ElasticSearchService elasticSearchService;

    public void processPaymentSuccess(PaymentSuccessEvent event) {
        log.info("✅ Processing Payment Success Event: {}", event);

        // ✅ ElasticSearch에 저장 (결제 패턴 분석을 위한 데이터)
        saveToElasticSearch(event);
    }

    private void saveToElasticSearch(PaymentSuccessEvent event) {
        log.info("📌 Storing Payment Data in ElasticSearch: {}", event);
        elasticSearchService.indexPaymentEvent(event);
    }
}
