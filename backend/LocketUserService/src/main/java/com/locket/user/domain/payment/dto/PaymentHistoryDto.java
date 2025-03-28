package com.locket.user.domain.payment.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentHistoryDto {

    private String transactionId;
    private long buyerId;
    private long sellerId;
    private String userJob;
    private String birthDate;
    private BigDecimal totalAmount;
    private String currency;
    private String paymentCategory;
    private String paymentMerchant;
    private int cardId;                // ✅ 추가됨
    private String cardName;           // ✅ 추가됨
    private String storeName;          // ✅ 추가됨
    private boolean receiptUploaded;   // ✅ 추가됨
    private String paymentStatus;
    private OffsetDateTime createdAt;
    private int year;                  // ✅ 추가됨
    private int month;                 // ✅ 추가됨
    private int day;                   // ✅ 추가됨
    private List<OrderDetailDto> orders;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderDetailDto {
        private String orderId;
        private String cardNumber;
        private BigDecimal amount;
        private String paymentOrderStatus;
    }
}
