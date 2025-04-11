package com.locket.payment.domain.pay.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResponse {
    private String transactionId;
    private String status;
    private String message;
}
