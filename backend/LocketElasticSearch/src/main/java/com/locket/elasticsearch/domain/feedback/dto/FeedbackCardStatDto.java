package com.locket.elasticsearch.domain.feedback.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FeedbackCardStatDto {
    private String cardName;
    private int usageCount;
    private int totalAmount;
}
