package com.locket.payment.domain.pay.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentPasswordRequest {
    private long userId;
    private int paymentPassword;
}
