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

        // [결제 후처리 1] 삼성카드로 결제한 경우 사료 추가
        if (isSamsungCardPayment(event)) {
            Long userId = event.getBuyerId();
            log.info("👉 삼성카드 결제! 사용자 {}에게 사료 지급 : {}", userId);

            try {
                characterService.addFood(userId);
                log.info("✅ 사료 추가 : {}", userId);
            } catch (Exception e) {
                log.error("❌ 사료 추가 실패: {}", userId, e);
                throw e;
            }
        } else {
            log.info("ℹ️ 삼성카드가 아닌 결제입니다. 카드명: {}, 사용자 ID: {}",
                    event.getCardName(), event.getBuyerId());
        }

        // [결제 후처리 2] ➤ 예산 초과 확인 및 알림 + 업데이트 서비스 호출
        budgetNotificationService.handleBudgetNotification(event);

        // [결제 후처리 3] 결제 완료 시 사용자에게 FCM 알림

    }

    private boolean isSamsungCardPayment(PaymentSuccessEvent event) {

        // cardName이 삼성카드인지
        String cardName = event.getCardName();
        if (cardName != null && cardName.toUpperCase().contains("SAMSUNG")) {
            return true;
        }
        log.debug("🔍 카드명 확인 결과: 삼성카드가 아닙니다. 카드명: {}",
                cardName != null ? cardName : "정보 없음");
        return false;
    }

}