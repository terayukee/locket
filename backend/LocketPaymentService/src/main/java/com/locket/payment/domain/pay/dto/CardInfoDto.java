package com.locket.payment.domain.pay.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CardInfoDto {
    private Integer cardId;
    private Integer userId;         // 🔹 userId 추가
    private String cardNumber;
    private String cardExpiry;
    private String cardCvc;         // 🔹 CVC 추가
    private String cardName;        // 🔹 카드 이름 추가
    private String accountNumber;
    private LocalDateTime createdAt;  // 🔹 생성일 추가
    private LocalDateTime updatedAt;  // 🔹 수정일 추가
}
