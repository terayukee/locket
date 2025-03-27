package com.locket.elasticsearch.domain.payment.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SummaryPaymentDto {
    private String paymentCategory;
    private String paymentMerchant;
    private Integer cardId;
    private String cardName;
    private String storeName;
    private BigDecimal totalAmount;
}
