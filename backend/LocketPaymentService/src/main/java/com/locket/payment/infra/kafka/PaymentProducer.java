package com.locket.payment.infra.kafka;

import com.locket.payment.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProducer {

    private final KafkaTemplate<String, PaymentSuccessEvent> kafkaTemplate;  // ✅ KafkaTemplate 타입 변경

    public void sendPaymentSuccessEvent(PaymentSuccessEvent event) {
        try {
            // ✅ JSON 변환 없이 객체 그대로 전송
            kafkaTemplate.send("payment.success", event);

            log.info("✅ Sent PaymentSuccessEvent: {}", event);
        } catch (Exception e) {
            log.error("❌ Failed to send PaymentSuccessEvent", e);
        }
    }
}
