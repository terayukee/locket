package com.locket.elasticsearch.domain.payment.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarPaymentDto {
    private int monthlyTotal;
    private List<DailySpending> dailySpending;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailySpending {
        private String date; // yyyy-MM-dd
        private int amount;
    }
}
