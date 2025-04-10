package com.locket.elasticsearch.domain.payment.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ReceiptUpdateRequest {
    private String transactionId;
    private List<ReceiptUpdateRequest.ReceiptItem> items;
    private int totalAmount;
    private Map<String, Integer> categoryAmount;

    @Getter
    @Setter
    public static class ReceiptItem {
        private Long itemId;
        private String itemName;
        private int itemQuantity;
        private int itemAmount;
        private String itemCategory;
    }
}