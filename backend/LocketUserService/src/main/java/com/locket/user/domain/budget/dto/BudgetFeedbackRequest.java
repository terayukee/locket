package com.locket.user.domain.budget.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.Map;

@Getter
@Builder
public class BudgetFeedbackRequest {
    private Map<String, Integer> categoryAmount;
    private BudgetMonthlyStatusDto budgetStatus;
    private String userJob;
}