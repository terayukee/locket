package com.locket.payment.domain.pay.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CardInfoDto {
    private Integer cardId;
    private String cardNumber;
    private String cardExpiry;
    private String accountNumber;
}
