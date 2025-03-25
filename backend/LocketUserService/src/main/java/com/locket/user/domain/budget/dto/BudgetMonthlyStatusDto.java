package com.locket.user.domain.budget.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@Builder
public class BudgetMonthlyStatusDto {

    private int year;
    private int month;
    private int target;           // 목표 금액
    private BigDecimal spent;     //사용 금액
    private BigDecimal remaining; //남은 예산
    private BigDecimal progress;

}
