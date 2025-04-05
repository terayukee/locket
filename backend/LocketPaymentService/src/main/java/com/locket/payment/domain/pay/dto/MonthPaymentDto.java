package com.locket.payment.domain.pay.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthPaymentDto {
    private String paymentCategory;
    private String cardName;
    private String storeName;
    private BigDecimal totalAmount;
    private Integer year;
    private Integer month;
}
