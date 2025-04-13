package com.locket.payment.domain.pay.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class CardInfoDto {
    private Integer cardId;
    private Integer userId;
    private String cardNumber;
    private String cardExpiry;
    private String cardCvc;
    private String cardName;
    private String accountNumber;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    private BigDecimal monthlyUsage; // 💳 해당 카드의 이번달 사용 금액
    private List<CardBenefitDto> benefits; // 💡 카드 혜택 리스트 추가
}
