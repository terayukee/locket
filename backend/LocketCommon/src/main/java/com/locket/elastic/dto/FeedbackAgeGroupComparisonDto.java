package com.locket.elastic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class FeedbackAgeGroupComparisonDto {
    private Map<String, Integer> userSpending;
    private Map<String, Double> ageGroupAverage;
}