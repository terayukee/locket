package com.locket.payment.service.pay;

import com.locket.payment.domain.pay.dto.QrPaymentRequest;
import com.locket.payment.event.PaymentSuccessEvent;
import com.locket.payment.infra.kafka.PaymentProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayService {
    private final PaymentProducer paymentProducer;

    public ResponseEntity<Map<String, Object>> processQrPayment(QrPaymentRequest request) {
        String transactionId = UUID.randomUUID().toString();

        System.out.println("✅ QR Payment Success - Transaction ID: " + transactionId);

        // ✅ 새로운 DTO 객체 생성
        PaymentSuccessEvent event = new PaymentSuccessEvent(
                transactionId,
                request.getAccountId(),
                request.getBuyerId(),
                request.getSellerId(),
                request.getPaymentCategory(),
                request.getPaymentMerchant()
        );

        // ✅ DTO를 직접 전달
        paymentProducer.sendPaymentSuccessEvent(event);

        // ✅ JSON 응답을 반환하기 위해 Map 생성
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Payment Successful!");
        response.put("transactionId", transactionId);

        return ResponseEntity.ok(response);
    }
}
