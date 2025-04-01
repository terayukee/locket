package com.locket.elastic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackCardStatDto {
    private String cardName;
    private Integer usageCount;
    private Double totalAmount;
}
