package com.locket.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.locket.kafka.event.PaymentSuccessEvent;

@Slf4j
@Service
public class PaymentProcessingService {

    public void processPaymentSuccess(PaymentSuccessEvent event) {
        log.info("✅ Processing Payment Success: {}", event);

        // 🏆 포인트 적립
        accumulateUserPoints(event.getBuyerId(), event.getPaymentCategory());

        // 🎖️ 결제 기반 미션 클리어
        checkUserMissions(event.getBuyerId(), event.getPaymentCategory());

        // 🏅 업적 달성 업데이트
        updateUserAchievements(event.getBuyerId());

        log.info("🎉 Payment Success Processed for User ID: {}", event.getBuyerId());
    }

    private void accumulateUserPoints(int userId, String category) {
        log.info("💰 Accumulating points for user {} in category {}", userId, category);
        // 포인트 적립 로직 추가
    }

    private void checkUserMissions(int userId, String category) {
        log.info("🏆 Checking missions for user {} in category {}", userId, category);
        // 미션 클리어 로직 추가
    }

    private void updateUserAchievements(int userId) {
        log.info("🏅 Updating achievements for user {}", userId);
        // 업적 업데이트 로직 추가
    }
}
