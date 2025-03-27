package com.locket.elasticsearch.service.payment;

import com.locket.elasticsearch.domain.payment.entity.PaymentHistory;
import com.locket.elasticsearch.domain.payment.entity.PaymentHistory.OrderDetail;
import com.locket.elasticsearch.domain.payment.repository.PaymentHistoryRepository;
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

    private final PaymentHistoryRepository paymentHistoryRepository;

    public void processPaymentSuccess(PaymentSuccessEvent event) {
        log.info("✅ Processing Payment Success Event: {}", event);

        String category = "카페";

        // DTO -> Elasticsearch Entity로 변환
        PaymentHistory history = PaymentHistory.builder()
                .transactionId(event.getTransactionId())
                .buyerId(event.getBuyerId())
                .sellerId(event.getSellerId())
                .userJob(event.getUserJob())
                .birthDate(event.getBirthDate())
                .totalAmount(event.getTotalAmount())
                .currency(event.getCurrency())
                .paymentCategory(category)
                .paymentMerchant(event.getPaymentMerchant())
                .cardId(event.getCardId())
                .cardName(event.getCardName())
                .storeName(event.getStoreName())                  // ✅ 매장명
                .receiptUploaded(event.isReceiptUploaded())       // ✅ 영수증 업로드 여부
                .paymentStatus(event.getPaymentStatus())
                .createdAt(event.getCreatedAt())
                .orders(convertOrderDetails(event.getOrders()))
                .build();

        paymentHistoryRepository.save(history);
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
