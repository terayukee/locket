package com.locket.user.service.payment;

import com.locket.kafka.event.PaymentSuccessEvent;
import com.locket.user.service.notification.BudgetNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProcessingService {

    private final BudgetNotificationService budgetNotificationService;

    public void processPaymentSuccess(PaymentSuccessEvent event) {
        log.info("✅ Processing Payment Success Event: {}", event);

        // ✅ Point, 미션 등 후속 처리할 로직 작성

        // ➤ 예산 초과 확인 및 알림 + 업데이트 서비스 호출
        budgetNotificationService.handleBudgetNotification(event);
    }

}
