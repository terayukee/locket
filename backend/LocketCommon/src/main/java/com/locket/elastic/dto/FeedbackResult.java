package com.locket.elastic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResult {
    private double totalAmount;
    private List<CategoryBreakdown> categoryBreakdown;
    private String summary;
    private List<String> insights;
    private List<String> recommendations;

    @Data
    @Builder
    public static class CategoryBreakdown {
        private String category;
        private double amount;
        private double percentage;
    }
}