package com.locket.elasticsearch.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locket.elasticsearch.service.payment.PaymentProcessingService;
import com.locket.kafka.event.PaymentSuccessEvent;
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

    @KafkaListener(topics = "payment.success", groupId = "elastic-search-group")
    public void listenPaymentSuccess(ConsumerRecord<String, PaymentSuccessEvent> record, Acknowledgment ack) {
        try {
            PaymentSuccessEvent paymentEvent = record.value();
            log.info("📥 Received Payment Success Event: {}", paymentEvent);

            // ✅ 결제 성공 이벤트 처리 (DB 및 ElasticSearch 저장)
            paymentProcessingService.processPaymentSuccess(paymentEvent);

            // ✅ Kafka 오프셋 커밋
            ack.acknowledge();
        } catch (Exception e) {
            log.error("❌ Error processing payment success event: {}", record.value(), e);
            // ❗ 예외 발생 시 ack.acknowledge()를 호출하지 않으면 Kafka가 자동 재시도
        }
    }
}
