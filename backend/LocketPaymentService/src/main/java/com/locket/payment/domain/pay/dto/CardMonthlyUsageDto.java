package com.locket.payment.domain.pay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardMonthlyUsageDto {

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("cardId")
    private Integer cardId;

    @JsonProperty("year")
    private Integer year;

    @JsonProperty("month")
    private Integer month;

    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;
}

