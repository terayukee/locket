package com.locket.user.domain.budget.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BudgetSetResponseDto {
    private Integer userId;
    private Integer amount;
}