package com.locket.user.service.feedback;

import com.locket.user.domain.auth.entity.User;
import com.locket.user.domain.auth.repository.UserRepository;
import com.locket.user.domain.feedback.entity.Feedback;
import com.locket.user.domain.feedback.repository.FeedbackRepository;
import com.locket.user.domain.goal.entity.Goal;
import com.locket.user.domain.goal.repository.GoalRepository;
import com.locket.user.domain.payment.dto.PaymentHistoryDto;
import com.locket.user.feign.PaymentHistoryFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final PaymentHistoryFeignClient paymentHistoryFeignClient;
    private final WebClient feedbackWebClient;

    public ResponseEntity<?> handleFeedbackRequest(Long userId, Integer year, Integer month) {

        Optional<Feedback> existing = feedbackRepository.findByUserIdAndFeedbackYearAndFeedbackMonth(userId, year, month);
        boolean isCurrentMonth = isCurrentYearMonth(year, month);

        if (existing.isEmpty() || (existing.isPresent() && isCurrentMonth)) {
            log.info("🔄 새 분석 수행 (없거나 이번 달)");
            return analyzeAndSaveFeedback(userId, year, month);
        }

        log.info("📄 기존 피드백 조회 (과거)");
        return ResponseEntity.ok(existing.get().getFeedbackText());
    }

    private ResponseEntity<?> analyzeAndSaveFeedback(Long userId, int year, int month) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

            Goal goal = goalRepository.findByUserIdAndGoalYearAndGoalMonth(userId, year, month)
                    .orElse(null); // 목표는 선택적

            List<PaymentHistoryDto> paymentHistories = paymentHistoryFeignClient.getPaymentHistories(userId, year, month);

            // Python 서비스로 분석 요청
            String feedbackJson = feedbackWebClient.post()
                    .uri("/api/feedback/spending")
                    .bodyValue(new FeedbackRequest(user, goal, paymentHistories)) // DTO 필요
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            Feedback feedback = Feedback.builder()
                    .userId(userId)
                    .goalId(goal != null ? goal.getGoalId() : null)
                    .feedbackText(feedbackJson)
                    .feedbackYear(year)
                    .feedbackMonth(month)
                    .build();

            feedbackRepository.save(feedback);
            return ResponseEntity.ok(feedbackJson);

        } catch (Exception e) {
            log.error("❌ 피드백 분석 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("INTERNAL_SERVER_ERROR");
        }
    }

    private boolean isCurrentYearMonth(int year, int month) {
        java.time.LocalDate now = java.time.LocalDate.now();
        return now.getYear() == year && now.getMonthValue() == month;
    }

    // WebClient 요청용 DTO (직렬화 필요)
    public record FeedbackRequest(
            User user,
            Goal goal,
            List<PaymentHistoryDto> payments
    ) {}
}
