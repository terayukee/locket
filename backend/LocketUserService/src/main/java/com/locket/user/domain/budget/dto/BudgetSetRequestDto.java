package com.locket.user.domain.budget.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BudgetSetRequestDto {

    @NotNull(message = "userId가 없음")
    private Integer userId;

    @NotNull(message = "amount가 없음")
    @Min(value = 1, message = "목표 금액은 1보다 커야 합니다")
    private Integer amount;
}
