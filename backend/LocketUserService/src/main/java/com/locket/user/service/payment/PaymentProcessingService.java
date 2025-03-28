package com.locket.user.service.payment;

import com.locket.kafka.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.locket.user.service.pet.CharacterService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProcessingService {

    private final CharacterService characterService;

    @KafkaListener(topics = "${spring.kafka.topics.payment-success}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consumePaymentSuccessEvent(PaymentSuccessEvent event, Acknowledgment ack) {
        try {
            log.info("✅ Payment Success Event received: {}", event);
            processPaymentSuccess(event);

            ack.acknowledge();
        } catch (Exception e) {
            log.error("❌ Error processing payment success event: {}", event, e);

            ack.acknowledge();
        }
    }

    public void processPaymentSuccess(PaymentSuccessEvent event) {
        log.info("✅ Processing Payment Success Event: {}", event);

        // 삼성카드로 결제한 경우 사료 추가
        if (isSamsungCardPayment(event)) {
            Long userId = event.getBuyerId();
            log.info("👉 Samsung Card payment detected. Adding food bonus for user: {}", userId);

            try {
                characterService.addFood(userId);
                log.info("✅ Food bonus added successfully for user: {}", userId);
            } catch (Exception e) {
                log.error("❌ Failed to add food bonus for user: {}", userId, e);
                throw e;
            }
        }

    }

    private boolean isSamsungCardPayment(PaymentSuccessEvent event) {

        // cardName이 삼성카드인지
        String cardName = event.getCardName();
        if (cardName != null && cardName.toUpperCase().contains("SAMSUNG")) {
            return true;
        }
        return false;
    }

}