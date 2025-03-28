package com.locket.user.service.payment;

import com.locket.kafka.event.PaymentSuccessEvent;
import com.locket.user.service.notification.BudgetNotificationService;
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

    private final BudgetNotificationService budgetNotificationService;
    private final CharacterService characterService;

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

        // ➤ 예산 초과 확인 및 알림 + 업데이트 서비스 호출
        budgetNotificationService.handleBudgetNotification(event);
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