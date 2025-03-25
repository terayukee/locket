package com.locket.kafka.event;

import com.locket.elasticsearch.dto.CategoryResponse;
import com.locket.elasticsearch.payment.entity.PaymentHistory;
import com.locket.elasticsearch.payment.repository.PaymentHistoryRepository;
import com.locket.kafka.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentKafkaConsumer {

    private final WebClient webClient;
    private final PaymentHistoryRepository paymentHistoryRepository;

    @KafkaListener(topics = "${spring.kafka.topic.payment-success}")
    public void consumePayment(PaymentSuccessEvent event, Acknowledgment ack) {
        try {
            // 1. 카테고리 분류 API 호출
            CategoryResponse categoryResponse = webClient.post()
                    .uri("/api/category/classify")
                    .bodyValue(event.getStoreName())
                    .retrieve()
                    .bodyToMono(CategoryResponse.class)
                    .block();

            // 2. PaymentHistory 객체 생성 및 저장
            PaymentHistory payment = PaymentHistory.builder()
                    .transactionId(event.getTransactionId())
                    .buyerId(event.getBuyerId())
                    .sellerId(event.getSellerId())
                    .totalAmount(event.getTotalAmount())
                    .paymentCategory(categoryResponse.getCategory())
                    .paymentMerchant(event.getPaymentMerchant())
                    .storeName(event.getStoreName())
                    .paymentStatus(event.getPaymentStatus())
                    .createdAt(event.getCreatedAt())
                    .build();

            paymentHistoryRepository.save(payment);
            ack.acknowledge();

        } catch (Exception e) {
            log.error("Payment processing failed: {}", e.getMessage(), e);
        }
    }
}