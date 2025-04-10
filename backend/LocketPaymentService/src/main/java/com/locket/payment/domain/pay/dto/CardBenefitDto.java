package com.locket.payment.domain.pay.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardBenefitDto {
    private Integer benefitId;
    private String item;
    private String benefitDetail;
}
