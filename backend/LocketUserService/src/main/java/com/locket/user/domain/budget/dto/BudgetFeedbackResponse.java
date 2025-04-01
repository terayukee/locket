package com.locket.user.domain.budget.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BudgetFeedbackResponse {
    private String nickname;
    private String feedback;
}