package com.locket.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleGoalDto {
    private Long goalId;
    private Long userId;
    private Integer goalAmount;
    private Integer goalYear;
    private Integer goalMonth;
}