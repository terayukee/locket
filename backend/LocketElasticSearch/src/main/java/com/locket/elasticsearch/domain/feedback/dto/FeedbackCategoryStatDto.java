package com.locket.elasticsearch.domain.feedback.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FeedbackCategoryStatDto {
    private String category;
    private int totalAmount;
    private double ratio; // 전체 소비 대비 비율 (%)
}
