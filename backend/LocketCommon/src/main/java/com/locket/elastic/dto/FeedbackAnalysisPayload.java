package com.locket.elastic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackAnalysisPayload {

    private FeedbackUserDto user;
    private FeedbackGoalDto goal;
    private List<FeedbackPaymentDto> payments;

    private List<FeedbackCategoryStatDto> categoryStats;
    private FeedbackDayOfWeekDto dayOfWeekStats;
    private List<FeedbackCardStatDto> cardStats;
    private String topStoreName;

    private Map<String, Integer> userSpending;
    private Map<String, Double> ageGroupAverage;

    private Integer currentMonthTotal;
    private Integer previousMonthTotal;
    private Double totalChangeRate;
    private Map<String, Double> categoryChangeRate;

    private List<String> hotCategories;
    private Double entropy;
}

