package com.locket.elastic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackAgeGroupComparisonDto {
    private Map<String, Integer> userSpending;
    private Map<String, Double> ageGroupAverage;
}