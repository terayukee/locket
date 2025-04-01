package com.locket.user.domain.product.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductAlertRequestDTO {
    private Long userId;
    private Boolean isAlert;
    private Integer alertPrice;
}