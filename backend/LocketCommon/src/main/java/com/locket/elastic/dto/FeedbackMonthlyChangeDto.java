package com.locket.elastic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackMonthlyChangeDto {
    private Double currentMonthTotal;
    private Double previousMonthTotal;
    private Double totalChangeRate;
    private Map<String, Double> categoryChangeRate;
}