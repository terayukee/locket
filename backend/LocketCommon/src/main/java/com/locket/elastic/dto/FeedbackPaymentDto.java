package com.locket.elastic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackPaymentDto {
    private String paymentCategory;
    private String storeName;
    private BigDecimal totalAmount;
    private OffsetDateTime createdAt;
}
