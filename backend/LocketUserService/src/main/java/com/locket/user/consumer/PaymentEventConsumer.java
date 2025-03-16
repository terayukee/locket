package com.locket.user.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locket.user.event.PaymentSuccessEvent;
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
    public void listenPaymentSuccess(PaymentSuccessEvent paymentEvent, Acknowledgment ack) {
    	System.out.println("✅ Received message: " + paymentEvent);
    	try {
            log.info("📥 Received Payment Success Event: {}", paymentEvent);

            // ✅ 결제 성공 후처리 (포인트 적립, 업적 업데이트 등)
            paymentProcessingService.processPaymentSuccess(paymentEvent);

            // ✅ 수동 커밋 (성공한 경우에만)
            ack.acknowledge();

        } catch (Exception e) {
            log.error("❌ Error processing payment success event: {}", paymentEvent, e);
            // ❗❗ 수동 커밋을 하지 않으면 Kafka가 재시도함.
        }
    }
}
