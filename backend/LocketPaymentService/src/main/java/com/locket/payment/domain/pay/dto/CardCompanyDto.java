package com.locket.payment.domain.pay.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardCompanyDto {
    private Integer companyId;
    private String companyName;
}
