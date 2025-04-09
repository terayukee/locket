package com.locket.user.domain.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetFeedbackResponse {
    private String nickname;
    private String feedback;
}