package com.locket.elasticsearch.domain.payment.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DayPaymentDto {
    private String paymentCategory;
    private String cardName;
    private String storeName;
    private BigDecimal totalAmount;
}
