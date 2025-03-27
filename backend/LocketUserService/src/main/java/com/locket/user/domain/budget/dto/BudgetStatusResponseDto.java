package com.locket.user.domain.budget.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BudgetStatusResponseDto {
    private int userId;
    private boolean hasBudget; // 예산 설정 여부
    private BudgetData budget;

    @Getter
    @Builder
    public static class BudgetData {
        private BudgetMonthlyStatusDto monthly;
    }
}