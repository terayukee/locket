package com.locket.payment.infra.kafka;

import com.locket.payment.domain.pay.dto.QrPaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendPaymentSuccessEvent(String transactionId, QrPaymentRequest request) {
        String message = String.format(
                "{ \"transactionId\": \"%s\", \"accountId\": %d, \"buyerId\": %d, \"sellerId\": %d, \"category\": \"%s\", \"merchant\": \"%s\" }",
                transactionId, request.getAccountId(), request.getBuyerId(), request.getSellerId(), request.getPaymentCategory(), request.getPaymentMerchant()
        );

        kafkaTemplate.send("payment.success", message);
        System.out.println("📢 Kafka Event Sent - payment.success: " + message);
    }
}
