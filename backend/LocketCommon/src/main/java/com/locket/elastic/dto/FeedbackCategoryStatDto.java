package com.locket.elastic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackCategoryStatDto {
    private String category;
    private Double amount;
    private Double ratio;
}
