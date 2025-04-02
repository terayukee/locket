package com.locket.user.service.feedback;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    /**
     * 피드백 요청 처리 메서드
     * 이미 존재하는 피드백이 있고 과거 달이라면 그대로 반환
     * 존재하지 않거나 이번 달이면 새로 분석 후 저장
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
     * 실제 피드백 분석 및 저장 수행
     */
    private ResponseEntity<?> analyzeAndSaveFeedback(Long userId, int year, int month) {
        try {
            // 사용자 조회
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

            // 해당 연월의 목표(goal) 조회
            Goal goal = goalRepository.findByUserIdAndGoalYearAndGoalMonth(userId, year, month).orElse(null);

            if (goal == null) {
                log.warn("사용자 {}의 {}년 {}월 목표(goal)가 존재하지 않아 피드백을 저장하지 않습니다.", userId, year, month);
                return ResponseEntity.status(400).body("해당 월의 목표(goal)가 존재하지 않아 피드백을 저장할 수 없습니다.");
            }

            // 피드백 분석에 필요한 데이터 조회 (페인/엘라스틱 서비스)
            List<PaymentHistoryDto> paymentHistories = paymentHistoryFeignClient.getPaymentHistories(userId, year, month);
            List<FeedbackCategoryStatDto> categoryStats = feedbackStatFeignClient.getCategoryStats(userId, year, month);
            FeedbackDayOfWeekDto dayOfWeekStats = feedbackStatFeignClient.getDayOfWeekStats(userId, year, month);
            List<FeedbackCardStatDto> cardStats = feedbackStatFeignClient.getCardStats(userId, year, month);
            TopStoreStatDto topStore = feedbackStatFeignClient.getTopSpendingStore(userId, year, month);
            FeedbackAgeGroupComparisonDto ageCompare = feedbackStatFeignClient.getAgeComparison(userId, user.getBirthYear(), year, month);
            FeedbackMonthlyChangeDto monthCompare = feedbackStatFeignClient.getPreviousMonthComparison(userId, year, month);
            HotCategoriesDto hotCategories = feedbackStatFeignClient.getHotCategories(userId, year, month);
            SpendingEntropyDto entropy = feedbackStatFeignClient.getSpendingEntropy(userId, year, month);

            // 총 지출 계산
            double totalSpent = paymentHistories.stream().mapToDouble(p -> p.getTotalAmount().doubleValue()).sum();

            // 카테고리별 지출 상세
            List<FeedbackCategoryBreakdownDto> breakdownList = categoryStats.stream()
                    .map(stat -> FeedbackCategoryBreakdownDto.builder()
                            .category(stat.getCategory())
                            .amount(stat.getAmount())
                            .percentage(Math.round(stat.getRatio() * 10.0) / 10.0)
                            .build())
                    .toList();

            List<String> insights = new ArrayList<>();         // 분석 인사이트
            List<String> recommendations = new ArrayList<>();  // 절약 추천

            // 특정 카테고리에 소비 집중 여부
            categoryStats.stream()
                    .filter(stat -> stat.getRatio() > 0.3)
                    .map(FeedbackCategoryStatDto::getCategory)
                    .reduce((a, b) -> a + ", " + b)
                    .ifPresent(overspent -> insights.add("🔥 '" + overspent + "' 카테고리에 전체 지출의 30% 이상이 몰렸습니다."));

            // 연령대 비교 분석
            ageCompare.getUserSpending().forEach((category, myAmt) -> {
                Double avgAmt = ageCompare.getAgeGroupAverage().get(category);
                if (avgAmt != null) {
                    if (myAmt > avgAmt) {
                        insights.add("🧐 " + category + " 항목에서 연령대 평균(" + Math.round(avgAmt) + "원)보다 많이 소비했습니다.");
                    } else {
                        insights.add("🧐 " + category + " 항목은 연령대 평균(" + Math.round(avgAmt) + "원)보다 적게 사용했습니다.");
                    }
                }
            });

            // 목표 대비 소비 확인
            int goalAmount = goal.getGoalAmount();
            if (totalSpent > goalAmount) {
                double over = totalSpent - goalAmount;
                double rate = over / goalAmount * 100;
                insights.add(String.format("🎯 목표(%d원)를 %.1f%% 초과한 %,.0f원 지출했습니다.", goalAmount, rate, totalSpent));
            } else {
                insights.add(String.format("🎯 목표 지출 %d원 이하로 소비하였습니다.", goalAmount));
            }

            // 요일별 소비 분석
            Map<String, Double> dayStats = dayOfWeekStats.getDayOfWeekStats();
            if (!dayStats.isEmpty()) {
                String maxDay = Collections.max(dayStats.entrySet(), Map.Entry.comparingByValue()).getKey();
                insights.add("📈 " + maxDay + "에 가장 많은 지출이 있었습니다.");
            }

            // 전월 대비 지출 비교
            if (monthCompare.getPreviousMonthTotal() > 0) {
                double delta = monthCompare.getTotalChangeRate() * 100;
                String trend = delta > 0 ? "증가" : "감소";
                insights.add(String.format("🍿 전월 대비 총 지출이 %.1f%% %s했습니다.", Math.abs(delta), trend));

                monthCompare.getCategoryChangeRate().forEach((cat, rate) -> {
                    if (Math.abs(rate) > 0.2) {
                        String symbol = rate > 0 ? "▲" : "▼";
                        insights.add(String.format(" - %s 지출 %s %.1f%%", cat, symbol, rate * 100));
                    }
                });
            }

            // 카드 소비 집중 여부
            if (!cardStats.isEmpty()) {
                FeedbackCardStatDto mostUsed = Collections.max(cardStats, Comparator.comparingInt(FeedbackCardStatDto::getUsageCount));
                int totalUsage = cardStats.stream().mapToInt(FeedbackCardStatDto::getUsageCount).sum();
                if (mostUsed.getUsageCount() > 0.7 * totalUsage) {
                    insights.add("⚖️ '" + mostUsed.getCardName() + "' 카드에 소비가 집중되었습니다.");
                }
            }

            // 가장 많이 사용된 매장
            if (topStore != null && topStore.getStoreName() != null) {
                insights.add("🏪 가장 많이 간 매장은 '" + topStore.getStoreName() + "'입니다.");
            }

            // 최근 소비 증가 카테고리
            if (!hotCategories.getCategories().isEmpty()) {
                insights.add("🔥 최근 3개월간 소비가 증가한 카테고리: " + String.join(", ", hotCategories.getCategories()));
            }

            // 소비 다양성
            insights.add(String.format("📈 소비 다양성 지수(Shannon entropy)는 %.2f입니다.", entropy.getEntropy()));

            // 절약 추천 (상위 소비 항목)
            categoryStats.stream()
                    .sorted(Comparator.comparingDouble(FeedbackCategoryStatDto::getAmount).reversed())
                    .limit(3)
                    .forEach(cat -> recommendations.add(cat.getCategory() + " 항목에서 " + String.format("%,.0f원", cat.getAmount()) + " 지출하였습니다. 절약 가능성을 검토해보세요."));

            // 간단 요약 문구
            String summary = String.format("%s님의 이번 달 총 지출은 %,.0f원입니다.", user.getNickname(), totalSpent);

            // 결과 JSON으로 직렬화
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("totalAmount", totalSpent);
            result.put("categoryBreakdown", breakdownList);
            result.put("summary", summary);
            result.put("insights", insights);
            result.put("recommendations", recommendations);

            String feedbackJson = objectMapper.writeValueAsString(result);

            // DB에 피드백 저장
            Feedback feedback = Feedback.builder()
                    .userId(userId)
                    .goalId(goal.getGoalId()) // 위에서 goal == null 체크했기 때문에 null 아님
                    .feedbackText(feedbackJson)
                    .feedbackYear(year)
                    .feedbackMonth(month)
                    .build();

            feedbackRepository.save(feedback);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(feedbackJson);

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
