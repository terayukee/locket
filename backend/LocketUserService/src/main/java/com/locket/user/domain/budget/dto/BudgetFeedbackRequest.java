package com.locket.user.domain.budget.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import java.util.Map;

@Getter
@Builder
public class BudgetFeedbackRequest {
    @JsonProperty("categoryAmount")
    private Map<String, Integer> totalCategoryAmount;
    private BudgetMonthlyStatusDto budgetStatus;
    private String userJob;
}