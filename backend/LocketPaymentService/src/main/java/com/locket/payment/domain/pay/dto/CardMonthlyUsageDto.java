package com.locket.payment.domain.pay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardMonthlyUsageDto {
    private Long userId;
    private Integer cardId;
    private Integer year;
    private Integer month;
    private BigDecimal totalAmount;
}
