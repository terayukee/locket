package com.locket.elastic.dto;

import com.locket.common.dto.SimpleGoalDto;
import com.locket.common.dto.SimpleUserDto;
import com.locket.payment.dto.PaymentHistoryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackAnalysisData {
    private SimpleUserDto user;
    private SimpleGoalDto goal;
    private List<PaymentHistoryDto> paymentHistories;
    private List<FeedbackCategoryStatDto> categoryStats;
    private FeedbackDayOfWeekDto dayOfWeekStats;
    private List<FeedbackCardStatDto> cardStats;
    private TopStoreStatDto topStore;
    private FeedbackAgeGroupComparisonDto ageCompare;
    private FeedbackMonthlyChangeDto monthCompare;
    private HotCategoriesDto hotCategories;
    private SpendingEntropyDto entropy;
}
