package com.locket.user.service.notification;

import com.locket.kafka.event.PaymentSuccessEvent;
import com.locket.user.domain.auth.entity.User;
import com.locket.user.domain.auth.repository.UserRepository;
import com.locket.user.domain.goal.entity.Goal;
import com.locket.user.domain.goal.repository.GoalRepository;
import com.locket.user.domain.notification.dto.FcmMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetNotificationService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public void handleBudgetNotification(PaymentSuccessEvent event) {
        Long userId = (long) event.getBuyerId();
        int usedAmount = event.getTotalAmount().intValue();
        int year = event.getCreatedAt().getYear();
        int month = event.getCreatedAt().getMonthValue();

        Goal goal = goalRepository.findByUserIdAndGoalYearAndGoalMonth(userId, year, month)
                .orElse(null);
        if (goal == null) {
            log.warn("❗ 예산 목표가 존재하지 않습니다. [userId={}, year={}, month={}]", userId, year, month);
            return;
        }

        int updatedUsedAmount = goal.getUsedAmount() + usedAmount;
        double usageRatio = (double) updatedUsedAmount / goal.getGoalAmount();

        // ✅ 알림 조건 판단
        boolean shouldNotify = (
                (usageRatio >= 0.9 && goal.getUsedAmount() < (int)(goal.getGoalAmount() * 0.9)) ||
                        (usageRatio >= 0.7 && goal.getUsedAmount() < (int)(goal.getGoalAmount() * 0.7))
        );

        if (shouldNotify) {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null || user.getFcmToken() == null) {
                log.warn("❗ 유저 정보 또는 FCM 토큰 없음. [userId={}]", userId);
                return;
            }

            String level = usageRatio >= 0.9 ? "90%" : "70%";
            String message = "이번 달 예산의 " + level + "을 초과했습니다! 절약을 시도해보세요 💸";

            FcmMessageDto dto = FcmMessageDto.builder()
                    .targetFcmToken(user.getFcmToken())
                    .title("💰 예산 초과 알림")
                    .body(message)
                    .build();

            notificationService.sendBudgetAlert(dto);
        }

        // ✅ 예산 사용량 업데이트
        goal.updateUsedAmount(updatedUsedAmount);
        goalRepository.save(goal);
    }
}
