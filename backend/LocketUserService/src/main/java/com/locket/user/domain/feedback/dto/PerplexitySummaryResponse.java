package com.locket.user.domain.feedback.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PerplexitySummaryResponse {
    private String insights;
    private String recommendations;
}