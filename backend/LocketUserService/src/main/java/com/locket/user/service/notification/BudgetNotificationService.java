package com.locket.user.service.notification;

import com.locket.kafka.event.PaymentSuccessEvent;
import com.locket.user.domain.auth.entity.User;
import com.locket.user.domain.auth.repository.UserRepository;
import com.locket.user.domain.goal.entity.Goal;
import com.locket.user.domain.goal.repository.GoalRepository;
import com.locket.user.domain.goalalert.entity.GoalAlert;
import com.locket.user.domain.goalalert.repository.GoalAlertRepository;
import com.locket.user.domain.notification.dto.FcmMessageDto;
import com.locket.user.domain.payment.dto.PaymentHistoryDto;
import com.locket.user.feign.PaymentHistoryFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetNotificationService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalAlertRepository goalAlertRepository;
    private final NotificationService notificationService;
    private final PaymentHistoryFeignClient paymentHistoryFeignClient;

    public void handleBudgetNotification(PaymentSuccessEvent event) {
        Long userId = (long) event.getBuyerId();
        int year = event.getCreatedAt().getYear();
        int month = event.getCreatedAt().getMonthValue();

        Goal goal = goalRepository.findByUserIdAndGoalYearAndGoalMonth(userId, year, month)
                .orElse(null);

        if (goal == null) {
            log.warn("❗ 예산 목표가 존재하지 않음: userId={}, year={}, month={}", userId, year, month);
            return;
        }

        // ✅ 이번 달 총 사용 금액 조회 (Elasticsearch API 호출)
        List<PaymentHistoryDto> histories = paymentHistoryFeignClient.getPaymentHistories(userId, year, month);
        BigDecimal totalUsed = histories.stream()
                .map(PaymentHistoryDto::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int goalAmount = goal.getGoalAmount();
        double usageRatio = totalUsed.doubleValue() / goalAmount;

        log.info("💡 예산 사용률 계산: userId={}, usedAmount={}, goalAmount={}, usageRatio={}%"
                , userId, totalUsed, goalAmount, Math.round(usageRatio * 100));

        // ✅ 70% / 90% 초과 판단
        boolean shouldNotify = (
                (usageRatio >= 0.9 && usageRatio < 1.0) ||
                        (usageRatio >= 0.7 && usageRatio < 0.9)
        );

        if (shouldNotify) {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null || user.getFcmToken() == null) {
                log.warn("❗ 사용자 정보 또는 FCM 토큰 없음: userId={}", userId);
                return;
            }

            String level = usageRatio >= 0.9 ? "90%" : "70%";
            String body = "이번 달 예산의 " + level + "를 초과했습니다! 절약을 시도해보세요 💸";

            // ✅ 중복 알림 확인
            if (goalAlertRepository.existsByUserIdAndGoalIdAndMessage(userId, goal.getGoalId(), body)) {
                log.info("⛔ 이미 동일한 예산 초과 알림이 발송됨 - 중복 방지: userId={}, goalId={}", userId, goal.getGoalId());
                return;
            }

            // ✅ FCM 전송
            FcmMessageDto dto = FcmMessageDto.builder()
                    .targetFcmToken(user.getFcmToken())
                    .title("💰 예산 초과 알림")
                    .body(body)
                    .build();
            notificationService.sendBudgetAlert(dto);

            // ✅ 알림 저장
            GoalAlert alert = GoalAlert.create(userId, goal.getGoalId(), body);
            goalAlertRepository.save(alert);
            log.info("📌 알림 저장 완료: userId={}, goalId={}", userId, goal.getGoalId());
        }
    }

    public static class NotificationResult {
        private final String status;
        private final String message;

        public NotificationResult(String status, String message) {
            this.status = status;
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }

    public NotificationResult testHandleBudgetNotification(PaymentSuccessEvent event) {
        Long userId = (long) event.getBuyerId();
        int year = event.getCreatedAt().getYear();
        int month = event.getCreatedAt().getMonthValue();

        Goal goal = goalRepository.findByUserIdAndGoalYearAndGoalMonth(userId, year, month)
                .orElse(null);

        if (goal == null) {
            return new NotificationResult("NO_GOAL", "❌ 예산 목표가 존재하지 않습니다.");
        }

        List<PaymentHistoryDto> histories = paymentHistoryFeignClient.getPaymentHistories(userId, year, month);
        BigDecimal totalUsed = histories.stream()
                .map(PaymentHistoryDto::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int goalAmount = goal.getGoalAmount();
        double usageRatio = totalUsed.doubleValue() / goalAmount;

        boolean shouldNotify = (
                (usageRatio >= 0.9 && usageRatio < 1.0) ||
                        (usageRatio >= 0.7 && usageRatio < 0.9)
        );

        if (!shouldNotify) {
            return new NotificationResult("NO_CONDITION",
                    "⛔ 알림 조건 미충족 (예산 사용률 " + Math.round(usageRatio * 100) + "%)");
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getFcmToken() == null) {
            return new NotificationResult("NO_USER", "❗ 사용자 정보 또는 FCM 토큰 없음");
        }

        String level = usageRatio >= 0.9 ? "90%" : "70%";
        String body = "이번 달 예산의 " + level + "를 초과했습니다! 절약을 시도해보세요 💸";

        if (goalAlertRepository.existsByUserIdAndGoalIdAndMessage(userId, goal.getGoalId(), body)) {
            return new NotificationResult("ALREADY_SENT", "⛔ 이미 동일한 예산 초과 알림이 전송됨");
        }

        FcmMessageDto dto = FcmMessageDto.builder()
                .targetFcmToken(user.getFcmToken())
                .title("💰 예산 초과 알림")
                .body(body)
                .build();
        notificationService.sendBudgetAlert(dto);

        goalAlertRepository.save(GoalAlert.create(userId, goal.getGoalId(), body));

        return new NotificationResult("SUCCESS", "✅ 알림 전송 및 저장 완료 (예산 사용률 " + Math.round(usageRatio * 100) + "%)");
    }

}
