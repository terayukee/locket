package com.locket.elasticsearch.service.payment;

import com.locket.elasticsearch.payment.entity.PaymentHistory;
import com.locket.elasticsearch.payment.entity.PaymentHistory.OrderDetail;
import com.locket.elasticsearch.payment.repository.PaymentElasticRepository;
import com.locket.kafka.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProcessingService {

    private final PaymentElasticRepository paymentElasticRepository;

    public void processPaymentSuccess(PaymentSuccessEvent event) {
        log.info("✅ Processing Payment Success Event: {}", event);

        // DTO -> Elasticsearch Entity로 변환
        PaymentHistory history = PaymentHistory.builder()
                .transactionId(event.getTransactionId())
                .buyerId(event.getBuyerId())
                .sellerId(event.getSellerId())
                .userJob(event.getUserJob())
                .birthDate(event.getBirthDate())
                .totalAmount(event.getTotalAmount())
                .currency(event.getCurrency())
                .paymentCategory(event.getPaymentCategory())
                .paymentMerchant(event.getPaymentMerchant())
                .paymentStatus(event.getPaymentStatus())
                .createdAt(event.getCreatedAt())
                .orders(convertOrderDetails(event.getOrders()))
                .build();

        paymentElasticRepository.save(history);
        log.info("✅ Saved PaymentHistory to Elasticsearch with ID: {}", history.getTransactionId());
    }

    private List<OrderDetail> convertOrderDetails(List<PaymentSuccessEvent.OrderDetail> dtoList) {
        return dtoList.stream().map(dto -> OrderDetail.builder()
                .orderId(dto.getOrderId())
                .cardNumber(dto.getCardNumber())
                .amount(dto.getAmount())
                .paymentOrderStatus(dto.getPaymentOrderStatus())
                .build()
        ).collect(Collectors.toList());
    }
}
