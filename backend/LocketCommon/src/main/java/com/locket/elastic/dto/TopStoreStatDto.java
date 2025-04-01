package com.locket.elastic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopStoreStatDto {
    private String storeName;
    private int totalAmount;
}
