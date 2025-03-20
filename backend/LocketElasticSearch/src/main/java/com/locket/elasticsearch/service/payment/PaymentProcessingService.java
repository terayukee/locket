package com.locket.elasticsearch.service.payment;

import com.locket.kafka.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProcessingService {

    public void processPaymentSuccess(PaymentSuccessEvent event) {
        log.info("✅ Processing Payment Success Event: {}", event);

        // ✅ Point, 미션 등 후속 처리할 로직 작성

    }

}
