package com.locket.elastic.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedbackCategoryBreakdownDto {
    private String category;
    private double amount;
    private double percentage;
}
