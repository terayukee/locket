package com.locket.user.domain.productalert.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAlertDto {
    private Long userId;
    private String message;
    private Boolean isRead;
    private Integer alertPrice;
}