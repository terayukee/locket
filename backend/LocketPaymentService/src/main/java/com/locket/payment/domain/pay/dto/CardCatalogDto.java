package com.locket.payment.domain.pay.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardCatalogDto {
    private Integer cardId;
    private String cardName;
    private String monthlyCondition;
    private CardCompanyDto company;
}
