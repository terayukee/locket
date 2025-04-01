package com.locket.user.domain.payment.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

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
    private long birthDate;
    private BigDecimal totalAmount;
    private String currency;
    private String paymentCategory;
    private String paymentMerchant;
    private int cardId;
    private String cardName;
    private String storeName;
    private boolean receiptUploaded;
    private String paymentStatus;
    private OffsetDateTime createdAt;
    private int year;
    private int month;
    private int day;

    private boolean needItemCheck;  // ✅ 추가
    private List<ReceiptItemDto> receiptItems;  // ✅ 추가
    private Map<String, Integer> categoryAmount; // ✅ 추가

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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReceiptItemDto {
        private Long itemId;
        private String itemName;
        private int itemQuantity;
        private int itemAmount;
        private String itemCategory;
    }
}
