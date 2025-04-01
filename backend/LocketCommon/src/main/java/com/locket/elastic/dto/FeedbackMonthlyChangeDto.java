package com.locket.elastic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackMonthlyChangeDto {
    private int currentMonthTotal;
    private int previousMonthTotal;
    private double totalChangeRate;
    private Map<String, Double> categoryChangeRate;
}