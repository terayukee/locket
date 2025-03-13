package com.locket.payment.service.pay;

import com.locket.payment.domain.pay.dto.QrPaymentRequest;
import com.locket.payment.infra.kafka.PaymentProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayService {
    private final PaymentProducer paymentProducer;

    public String processQrPayment(QrPaymentRequest request) {
        String transactionId = UUID.randomUUID().toString();

        System.out.println("✅ QR Payment Success - Transaction ID: " + transactionId);

        paymentProducer.sendPaymentSuccessEvent(transactionId, request);

        return "Payment Successful! Transaction ID: " + transactionId;
    }
}
