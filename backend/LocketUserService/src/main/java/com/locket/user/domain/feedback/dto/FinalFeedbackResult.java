package com.locket.user.domain.feedback.dto;

import com.locket.elastic.dto.FeedbackResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinalFeedbackResult {
    private double totalAmount;
    private List<FeedbackResult.CategoryBreakdown> categoryBreakdown;
    private String summary;
    private String insights;         // ✅ 단일 String
    private String recommendations;  // ✅ 단일 String
}
