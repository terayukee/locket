package com.locket.user.service.feedback;

import com.locket.elastic.dto.*;
import com.locket.user.domain.auth.entity.User;
import com.locket.user.domain.auth.repository.UserRepository;
import com.locket.user.domain.feedback.entity.Feedback;
import com.locket.user.domain.feedback.repository.FeedbackRepository;
import com.locket.user.domain.goal.entity.Goal;
import com.locket.user.domain.goal.repository.GoalRepository;
import com.locket.user.domain.payment.dto.PaymentHistoryDto;
import com.locket.user.feign.FeedbackStatFeignClient;
import com.locket.user.feign.PaymentHistoryFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final PaymentHistoryFeignClient paymentHistoryFeignClient;
    private final FeedbackStatFeignClient feedbackStatFeignClient;
    private final WebClient feedbackWebClient;

    public ResponseEntity<?> handleFeedbackRequest(Long userId, Integer year, Integer month) {

        Optional<Feedback> existing = feedbackRepository.findByUserIdAndFeedbackYearAndFeedbackMonth(userId, year, month);
        boolean isCurrentMonth = isCurrentYearMonth(year, month);

        if (existing.isEmpty() || (existing.isPresent() && isCurrentMonth)) {
            log.info("\uD83D\uDD04 새 분석 수행 (없거나 이번 달)");
            return analyzeAndSaveFeedback(userId, year, month);
        }

        log.info("\uD83D\uDCC4 기존 피드백 조회 (과거)");
        return ResponseEntity.ok(existing.get().getFeedbackText());
    }

    private ResponseEntity<?> analyzeAndSaveFeedback(Long userId, int year, int month) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

            Goal goal = goalRepository.findByUserIdAndGoalYearAndGoalMonth(userId, year, month)
                    .orElse(null); // 목표는 선택적

            List<PaymentHistoryDto> paymentHistories = paymentHistoryFeignClient.getPaymentHistories(userId, year, month);

            // ElasticSearch 분석 API 호출
            List<FeedbackCategoryStatDto> categoryStats = feedbackStatFeignClient.getCategoryStats(userId, year, month);
            FeedbackDayOfWeekDto dayOfWeekStats = feedbackStatFeignClient.getDayOfWeekStats(userId, year, month);
            List<FeedbackCardStatDto> cardStats = feedbackStatFeignClient.getCardStats(userId, year, month);
            String topStore = feedbackStatFeignClient.getTopSpendingStore(userId, year, month);
            var ageCompare = feedbackStatFeignClient.getAgeComparison(userId, user.getBirthYear(), year, month);
            var monthCompare = feedbackStatFeignClient.getPreviousMonthComparison(userId, year, month);
            List<String> hotCategories = feedbackStatFeignClient.getHotCategories(userId, year, month);
            Double entropy = feedbackStatFeignClient.getSpendingEntropy(userId, year, month);

            // 공통 DTO 매핑
            FeedbackUserDto userDto = FeedbackUserDto.builder()
                    .userId(user.getUserId())
                    .nickname(user.getNickname())
                    .birthYear(user.getBirthYear())
                    .userJob(user.getUserJob().toString())
                    .build();

            FeedbackGoalDto goalDto = goal != null ? FeedbackGoalDto.builder()
                    .goalId(goal.getGoalId())
                    .goalAmount(goal.getGoalAmount())
                    .build() : null;

            List<FeedbackPaymentDto> payments = paymentHistories.stream()
                    .map(p -> FeedbackPaymentDto.builder()
                            .paymentCategory(p.getPaymentCategory())
                            .storeName(p.getStoreName())
                            .totalAmount(p.getTotalAmount())
                            .createdAt(p.getCreatedAt())
                            .build())
                    .collect(Collectors.toList());

            FeedbackAnalysisPayload payload = FeedbackAnalysisPayload.builder()
                    .user(userDto)
                    .goal(goalDto)
                    .payments(payments)
                    .categoryStats(categoryStats)
                    .dayOfWeekStats(dayOfWeekStats)
                    .cardStats(cardStats)
                    .topStoreName(topStore)
                    .userSpending((Map<String, Integer>) ageCompare.get("userSpending"))
                    .ageGroupAverage((Map<String, Double>) ageCompare.get("ageGroupAverage"))
                    .currentMonthTotal((Integer) monthCompare.get("currentMonthTotal"))
                    .previousMonthTotal((Integer) monthCompare.get("previousMonthTotal"))
                    .totalChangeRate((Double) monthCompare.get("totalChangeRate"))
                    .categoryChangeRate((Map<String, Double>) monthCompare.get("categoryChangeRate"))
                    .hotCategories(hotCategories)
                    .entropy(entropy)
                    .build();

            log.info("📦 분석 요청 페이로드: {}", payload);

            // Python 서비스로 분석 요청
            String feedbackJson = feedbackWebClient.post()
                    .uri("/api/feedback/spending")
                    .bodyValue(payload)
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
}
