package com.locket.user.domain.budget.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BudgetStatusResponseDto {
    private long userId;
    private BudgetData budget;

    @Getter
    @Builder
    public static class BudgetData {
        private BudgetMonthlyStatusDto monthly;
    }
}