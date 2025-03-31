package com.locket.elasticsearch.domain.feedback.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.DayOfWeek;
import java.util.EnumMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class FeedbackDayOfWeekDto {
    private Map<DayOfWeek, Integer> spendingByDay = new EnumMap<>(DayOfWeek.class);
}
