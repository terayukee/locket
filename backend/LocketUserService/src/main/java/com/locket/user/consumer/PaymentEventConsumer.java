package com.locket.user.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locket.user.event.PaymentSuccessEvent;
import com.locket.user.service.PaymentProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final PaymentProcessingService paymentProcessingService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "payment.success", groupId = "user-service-group")
    public void listenPaymentSuccess(String message) {
        try {
            // JSON 메시지를 DTO로 변환
            PaymentSuccessEvent event = objectMapper.readValue(message, PaymentSuccessEvent.class);
            log.info("📥 Received Payment Success Event: {}", event);

            // 결제 성공 후처리 (포인트 적립, 업적 업데이트 등)
            paymentProcessingService.processPaymentSuccess(event);

        } catch (Exception e) {
            log.error("❌ Error processing payment success event", e);
        }
    }
}
