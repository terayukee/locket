package com.locket.elasticsearch.domain.payment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReceiptPaymentDto {
    private String transactionId;
    private String storeName;
    private String paymentCategory;
    private String cardName;
    private String paymentDate;
    private int amount;
}