package com.locket.user.service.feedback;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locket.user.client.PerplexityClient;
import com.locket.user.dto.SimpleGoalDto;
import com.locket.user.dto.SimpleUserDto;
import com.locket.elastic.dto.*;
import com.locket.payment.dto.PaymentHistoryDto;
import com.locket.user.domain.auth.entity.User;
import com.locket.user.domain.auth.repository.UserRepository;
import com.locket.user.domain.feedback.entity.Feedback;
import com.locket.user.domain.feedback.repository.FeedbackRepository;
import com.locket.user.domain.goal.entity.Goal;
import com.locket.user.domain.goal.repository.GoalRepository;
import com.locket.user.feign.FeedbackStatFeignClient;
import com.locket.user.feign.PaymentHistoryFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private final FeedbackStatFeignClient feedbackStatFeignClient;
    private final FeedbackAnalyzer feedbackAnalyzer;
    private final ObjectMapper objectMapper;
    private final PerplexityClient perplexityClient;

    /**
     * 피드백 요청 처리 메서드
     */
    public ResponseEntity<?> handleFeedbackRequest(Long userId, Integer year, Integer month) {
        Optional<Feedback> existing = feedbackRepository.findByUserIdAndFeedbackYearAndFeedbackMonth(userId, year, month);
        boolean isCurrentMonth = isCurrentYearMonth(year, month);

        if (existing.isEmpty() || (existing.isPresent() && isCurrentMonth)) {
            log.info("🔄 새 분석 수행 (없거나 이번 달)");
            return analyzeAndSaveFeedback(userId, year, month);
        }

        log.info("📄 기존 피드백 조회 (과거)");
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(existing.get().getFeedbackText());
    }

    /**
     * 피드백 분석 및 저장 로직
     */
    private ResponseEntity<?> analyzeAndSaveFeedback(Long userId, int year, int month) {
        try {
            // 사용자 및 목표 조회
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
            Goal goal = goalRepository.findByUserIdAndGoalYearAndGoalMonth(userId, year, month).orElse(null);
            if (goal == null) {
                log.warn("사용자 {}의 {}년 {}월 목표(goal)가 존재하지 않아 피드백을 저장하지 않습니다.", userId, year, month);
                return ResponseEntity.badRequest().body("해당 월의 목표(goal)가 존재하지 않습니다.");
            }

            // 분석 데이터 수집
            List<PaymentHistoryDto> paymentHistories = paymentHistoryFeignClient.getPaymentHistories(userId, year, month);
            List<FeedbackCategoryStatDto> categoryStats = feedbackStatFeignClient.getCategoryStats(userId, year, month);
            FeedbackDayOfWeekDto dayOfWeekStats = feedbackStatFeignClient.getDayOfWeekStats(userId, year, month);
            List<FeedbackCardStatDto> cardStats = feedbackStatFeignClient.getCardStats(userId, year, month);
            TopStoreStatDto topStore = feedbackStatFeignClient.getTopSpendingStore(userId, year, month);
            FeedbackAgeGroupComparisonDto ageCompare = feedbackStatFeignClient.getAgeComparison(userId, user.getBirthYear(), year, month);
            FeedbackMonthlyChangeDto monthCompare = feedbackStatFeignClient.getPreviousMonthComparison(userId, year, month);
            HotCategoriesDto hotCategories = feedbackStatFeignClient.getHotCategories(userId, year, month);
            SpendingEntropyDto entropy = feedbackStatFeignClient.getSpendingEntropy(userId, year, month);

            // 사용자 정보
            SimpleUserDto simpleUser = SimpleUserDto.builder()
                    .userId(user.getUserId())
                    .nickname(user.getNickname())
                    .birthYear(user.getBirthYear())
                    .userJob(String.valueOf(user.getUserJob()))
                    .build();

            // 설정 목표 정보
            SimpleGoalDto simpleGoal = SimpleGoalDto.builder()
                    .goalId(goal.getGoalId())
                    .goalAmount(goal.getGoalAmount())
                    .goalYear(goal.getGoalYear())
                    .goalMonth(goal.getGoalMonth())
                    .build();

            // 분석 실행
            FeedbackAnalysisData data = FeedbackAnalysisData.builder()
                    .user(simpleUser)
                    .goal(simpleGoal)
                    .paymentHistories(paymentHistories)
                    .categoryStats(categoryStats)
                    .dayOfWeekStats(dayOfWeekStats)
                    .cardStats(cardStats)
                    .topStore(topStore)
                    .ageCompare(ageCompare)
                    .monthCompare(monthCompare)
                    .hotCategories(hotCategories)
                    .entropy(entropy)
                    .build();

            FeedbackResult result = feedbackAnalyzer.analyze(data);

            // Perplexity 요약 결과 생성
            String summaryText = perplexityClient.summarizeFeedback(result.getInsights(), result.getRecommendations());

            // 새로운 JSON 구조 생성
            FeedbackResult summarized = FeedbackResult.builder()
                    .totalAmount(result.getTotalAmount())
                    .categoryBreakdown(result.getCategoryBreakdown())
                    .summary(result.getSummary())
                    .insights(List.of(summaryText))
                    .recommendations(List.of())
                    .build();

            String jsonResult = objectMapper.writeValueAsString(summarized);

            // DB 저장
            Feedback feedback = Feedback.builder()
                    .userId(userId)
                    .goalId(goal.getGoalId())
                    .feedbackText(jsonResult)
                    .feedbackYear(year)
                    .feedbackMonth(month)
                    .build();
            feedbackRepository.save(feedback);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(jsonResult);

        } catch (Exception e) {
            log.error("❌ 피드백 분석 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("INTERNAL_SERVER_ERROR");
        }
    }

    /**
     * 현재 연도와 월인지 확인
     */
    private boolean isCurrentYearMonth(int year, int month) {
        LocalDate now = LocalDate.now();
        return now.getYear() == year && now.getMonthValue() == month;
    }
}
