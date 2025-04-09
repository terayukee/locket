package com.locket.elasticsearch.domain.payment.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class ReceiptPaymentDto {
    private List<Receipt> receipts;

    @Getter
    @Builder
    public static class Receipt {
        private String transactionId;
        private String storeName;
        private String paymentCategory;
        private String cardName;
        private String paymentDate;
        private int amount;
    }
}