package com.locket.user.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locket.kafka.event.PaymentSuccessEvent;
import com.locket.user.service.PaymentProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final PaymentProcessingService paymentProcessingService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "payment.success", groupId = "user-service-group")
    public void listenPaymentSuccess(ConsumerRecord<String, PaymentSuccessEvent> record, Acknowledgment ack) {
        PaymentSuccessEvent paymentEvent = record.value();
        log.info("📥 Received Payment Success Event: {}", paymentEvent);

        try {
            paymentProcessingService.processPaymentSuccess(paymentEvent);
            ack.acknowledge();  // ✅ 즉시 수동 커밋 수행
        } catch (Exception e) {
            log.error("❌ Error processing payment success event: {}", paymentEvent, e);
            // ❗ 예외 발생 시 ack.acknowledge()를 호출하지 않으면 Kafka가 자동 재시도
        }
    }

}
