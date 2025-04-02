package com.locket.user.service.feedback;

import com.locket.elastic.dto.FeedbackAnalysisData;
import com.locket.elastic.dto.FeedbackCardStatDto;
import com.locket.elastic.dto.FeedbackCategoryStatDto;
import com.locket.elastic.dto.FeedbackResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FeedbackAnalyzer {

    public FeedbackResult analyze(FeedbackAnalysisData data) {
        double totalSpent = data.getPaymentHistories().stream().mapToDouble(p -> p.getTotalAmount().doubleValue()).sum();

        List<FeedbackResult.CategoryBreakdown> breakdownList = data.getCategoryStats().stream()
                .map(stat -> FeedbackResult.CategoryBreakdown.builder()
                        .category(stat.getCategory())
                        .amount(stat.getAmount())
                        .percentage(Math.round(stat.getRatio() * 10.0) / 10.0)
                        .build())
                .collect(Collectors.toList());

        List<String> insights = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();

        data.getCategoryStats().stream()
                .filter(stat -> stat.getRatio() > 0.3)
                .map(FeedbackCategoryStatDto::getCategory)
                .reduce((a, b) -> a + ", " + b)
                .ifPresent(overspent -> insights.add("🔥 '" + overspent + "' 카테고리에 전체 지출의 30% 이상이 몰렸습니다."));

        data.getAgeCompare().getUserSpending().forEach((category, myAmt) -> {
            Double avgAmt = data.getAgeCompare().getAgeGroupAverage().get(category);
            if (avgAmt != null) {
                if (myAmt > avgAmt) {
                    insights.add("🧐 " + category + " 항목에서 연령대 평균(" + Math.round(avgAmt) + "원)보다 많이 소비했습니다.");
                } else {
                    insights.add("🧐 " + category + " 항목은 연령대 평균(" + Math.round(avgAmt) + "원)보다 적게 사용했습니다.");
                }
            }
        });

        int goalAmount = data.getGoal().getGoalAmount();
        if (totalSpent > goalAmount) {
            double over = totalSpent - goalAmount;
            double rate = over / goalAmount * 100;
            insights.add(String.format("🎯 목표(%d원)를 %.1f%% 초과한 %,.0f원 지출했습니다.", goalAmount, rate, totalSpent));
        } else {
            insights.add(String.format("🎯 목표 지출 %d원 이하로 소비하였습니다.", goalAmount));
        }

        Map<String, Double> dayStats = data.getDayOfWeekStats().getDayOfWeekStats();
        if (!dayStats.isEmpty()) {
            String maxDay = Collections.max(dayStats.entrySet(), Map.Entry.comparingByValue()).getKey();
            insights.add("📈 " + maxDay + "에 가장 많은 지출이 있었습니다.");
        }

        if (data.getMonthCompare().getPreviousMonthTotal() > 0) {
            double delta = data.getMonthCompare().getTotalChangeRate() * 100;
            String trend = delta > 0 ? "증가" : "감소";
            insights.add(String.format("🍿 전월 대비 총 지출이 %.1f%% %s했습니다.", Math.abs(delta), trend));

            data.getMonthCompare().getCategoryChangeRate().forEach((cat, rate) -> {
                if (Math.abs(rate) > 0.2) {
                    String symbol = rate > 0 ? "▲" : "▼";
                    insights.add(String.format(" - %s 지출 %s %.1f%%", cat, symbol, rate * 100));
                }
            });
        }

        if (!data.getCardStats().isEmpty()) {
            FeedbackCardStatDto mostUsed = Collections.max(data.getCardStats(), Comparator.comparingInt(FeedbackCardStatDto::getUsageCount));
            int totalUsage = data.getCardStats().stream().mapToInt(FeedbackCardStatDto::getUsageCount).sum();
            if (mostUsed.getUsageCount() > 0.7 * totalUsage) {
                insights.add("⚖️ '" + mostUsed.getCardName() + "' 카드에 소비가 집중되었습니다.");
            }
        }

        if (data.getTopStore() != null && data.getTopStore().getStoreName() != null) {
            insights.add("🏪 가장 많이 간 매장은 '" + data.getTopStore().getStoreName() + "'입니다.");
        }

        if (!data.getHotCategories().getCategories().isEmpty()) {
            insights.add("🔥 최근 3개월간 소비가 증가한 카테고리: " + String.join(", ", data.getHotCategories().getCategories()));
        }

        insights.add(String.format("📈 소비 다양성 지수(Shannon entropy)는 %.2f입니다.", data.getEntropy().getEntropy()));

        data.getCategoryStats().stream()
                .sorted(Comparator.comparingDouble(FeedbackCategoryStatDto::getAmount).reversed())
                .limit(3)
                .forEach(cat -> recommendations.add(cat.getCategory() + " 항목에서 " + String.format("%,.0f원", cat.getAmount()) + " 지출하였습니다. 절약 가능성을 검토해보세요."));

        String summary = String.format("%s님의 이번 달 총 지출은 %,.0f원입니다.", data.getUser().getNickname(), totalSpent);

        return FeedbackResult.builder()
                .totalAmount(totalSpent)
                .categoryBreakdown(breakdownList)
                .summary(summary)
                .insights(insights)
                .recommendations(recommendations)
                .build();
    }
}