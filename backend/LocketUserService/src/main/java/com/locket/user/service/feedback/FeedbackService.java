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

import java.time.LocalDate;
import java.util.*;
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

            Goal goal = goalRepository.findByUserIdAndGoalYearAndGoalMonth(userId, year, month).orElse(null);

            List<PaymentHistoryDto> paymentHistories = paymentHistoryFeignClient.getPaymentHistories(userId, year, month);
            List<FeedbackCategoryStatDto> categoryStats = feedbackStatFeignClient.getCategoryStats(userId, year, month);
            FeedbackDayOfWeekDto dayOfWeekStats = feedbackStatFeignClient.getDayOfWeekStats(userId, year, month);
            List<FeedbackCardStatDto> cardStats = feedbackStatFeignClient.getCardStats(userId, year, month);
            TopStoreStatDto topStore = feedbackStatFeignClient.getTopSpendingStore(userId, year, month);
            FeedbackAgeGroupComparisonDto ageCompare = feedbackStatFeignClient.getAgeComparison(userId, user.getBirthYear(), year, month);
            FeedbackMonthlyChangeDto monthCompare = feedbackStatFeignClient.getPreviousMonthComparison(userId, year, month);
            HotCategoriesDto hotCategories = feedbackStatFeignClient.getHotCategories(userId, year, month);
            SpendingEntropyDto entropy = feedbackStatFeignClient.getSpendingEntropy(userId, year, month);

            double totalSpent = paymentHistories.stream().mapToDouble(p -> p.getTotalAmount().doubleValue()).sum();
            List<String> insights = new ArrayList<>();
            List<String> recommendations = new ArrayList<>();

            // 1. 과소비 항목
            categoryStats.stream()
                    .filter(stat -> stat.getRatio() > 0.3)
                    .map(stat -> stat.getCategory())
                    .reduce((a, b) -> a + ", " + b)
                    .ifPresent(overspent -> insights.add("🔥 '" + overspent + "' 카테고리에 전체 지출의 30% 이상이 몰렸습니다."));

            // 2. 연령대 평균 비교
            ageCompare.getUserSpending().forEach((category, myAmt) -> {
                Double avgAmt = ageCompare.getAgeGroupAverage().get(category);
                if (avgAmt != null) {
                    if (myAmt > avgAmt) {
                        insights.add("🧍‍♂️ " + category + " 항목에서 연령대 평균(" + Math.round(avgAmt) + "원)보다 많이 소비했습니다.");
                    } else {
                        insights.add("🧍‍♂️ " + category + " 항목은 연령대 평균(" + Math.round(avgAmt) + "원)보다 적게 사용했습니다.");
                    }
                }
            });

            // 3. 목표 초과 여부
            if (goal != null) {
                int goalAmount = goal.getGoalAmount();
                if (totalSpent > goalAmount) {
                    double over = totalSpent - goalAmount;
                    double rate = over / goalAmount * 100;
                    insights.add(String.format("🎯 목표(%d원)를 %.1f%% 초과한 %,.0f원 지출했습니다.", goalAmount, rate, totalSpent));
                } else {
                    insights.add(String.format("🎯 목표 지출 %d원 이하로 소비하였습니다.", goalAmount));
                }
            }

            // 4. 요일별 소비 패턴
            Map<String, Double> dayStats = dayOfWeekStats.getDayOfWeekStats();
            if (!dayStats.isEmpty()) {
                String maxDay = Collections.max(dayStats.entrySet(), Map.Entry.comparingByValue()).getKey();
                insights.add("📊 " + maxDay + "에 가장 많은 지출이 있었습니다.");
            }

            // 5. 전월 대비 증감 분석
            if (monthCompare.getPreviousMonthTotal() > 0) {
                double delta = monthCompare.getTotalChangeRate() * 100;
                String trend = delta > 0 ? "증가" : "감소";
                insights.add(String.format("🏷️ 전월 대비 총 지출이 %.1f%% %s했습니다.", Math.abs(delta), trend));
                monthCompare.getCategoryChangeRate().forEach((cat, rate) -> {
                    if (Math.abs(rate) > 0.2) {
                        String symbol = rate > 0 ? "▲" : "▼";
                        insights.add(String.format(" - %s 지출 %s %.1f%%", cat, symbol, rate * 100));
                    }
                });
            }

            // 6. 카드 사용 집중도
            if (!cardStats.isEmpty()) {
                FeedbackCardStatDto mostUsed = Collections.max(cardStats, Comparator.comparingInt(FeedbackCardStatDto::getUsageCount));
                int totalUsage = cardStats.stream().mapToInt(FeedbackCardStatDto::getUsageCount).sum();
                if (mostUsed.getUsageCount() > 0.7 * totalUsage) {
                    insights.add("⚖️ '" + mostUsed.getCardName() + "' 카드에 소비가 집중되었습니다.");
                }
            }

            // 7. 자주 간 가게
            if (topStore != null && topStore.getStoreName() != null) {
                insights.add("🏪 가장 많이 간 매장은 '" + topStore.getStoreName() + "'입니다.");
            }

            // 8. Hot 카테고리
            if (!hotCategories.getCategories().isEmpty()) {
                insights.add("🔥 최근 3개월간 소비가 증가한 카테고리: " + String.join(", ", hotCategories.getCategories()));
            }

            // 9. 소비 다양성 지수
            insights.add(String.format("📈 소비 다양성 지수(Shannon entropy)는 %.2f입니다.", entropy.getEntropy()));

            // 추천: 절약 가능한 상위 카테고리
            categoryStats.stream()
                    .sorted(Comparator.comparingDouble(FeedbackCategoryStatDto::getAmount).reversed())
                    .limit(3)
                    .forEach(cat -> recommendations.add(cat.getCategory() + " 항목에서 " + String.format("%,.0f원", cat.getAmount()) + " 지출하였습니다. 절약 가능성을 검토해보세요."));

            String summary = String.format("%s님의 이번 달 총 지출은 %,.0f원입니다.", user.getNickname(), totalSpent);
            Map<String, Object> result = Map.of(
                    "summary", summary,
                    "insights", insights,
                    "recommendations", recommendations
            );

            Feedback feedback = Feedback.builder()
                    .userId(userId)
                    .goalId(goal != null ? goal.getGoalId() : null)
                    .feedbackText(result.toString())
                    .feedbackYear(year)
                    .feedbackMonth(month)
                    .build();

            feedbackRepository.save(feedback);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("❌ 피드백 분석 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("INTERNAL_SERVER_ERROR");
        }
    }

    private boolean isCurrentYearMonth(int year, int month) {
        LocalDate now = LocalDate.now();
        return now.getYear() == year && now.getMonthValue() == month;
    }
}