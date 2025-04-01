package com.locket.elasticsearch.service.payment;

import com.locket.elasticsearch.domain.payment.entity.PaymentHistory;
import com.locket.elasticsearch.domain.payment.entity.PaymentHistory.OrderDetail;
import com.locket.elasticsearch.domain.payment.repository.PaymentHistoryRepository;
import com.locket.kafka.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProcessingService {

    private final PaymentHistoryRepository paymentHistoryRepository;
    private final WebClient webClient;

    public void processPaymentSuccess(PaymentSuccessEvent event) {
        log.info("✅ Processing Payment Success Event: {}", event);

        try {
            // FastAPI 호출 - Map으로 직접 요청/응답 처리
            log.info("📤 Sending category classification request for store: {}", event.getStoreName());
            Map<String, String> request = Map.of("storeName", event.getStoreName());

            Map<String, Object> response = webClient.post()
                    .uri("/api/category/classify")
                    .bodyValue(Map.of("storeName", event.getStoreName()))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
            log.info("📥 Received category classification response: {}", response);
            log.info("📥 Category from response: {}", response.get("paymentCategory"));

            // 초기 categoryAmount 설정 (단일 카테고리)
            Map<String, Integer> categoryAmount = Map.of(
                (String) response.get("paymentCategory"),
                event.getTotalAmount().intValue()
            );

            // DTO -> Elasticsearch Entity로 변환
            PaymentHistory history = PaymentHistory.builder()
                    .transactionId(event.getTransactionId())
                    .buyerId(event.getBuyerId())
                    .sellerId(event.getSellerId())
                    .userJob(event.getUserJob())
                    .birthDate(event.getBirthDate())
                    .totalAmount(event.getTotalAmount())
                    .currency(event.getCurrency())
//                    .paymentCategory((String) response.get("paymentCategory"))  // API 응답으로 받은 카테고리 사용
                    .paymentCategory(event.getPaymentCategory())
//                    .needItemCheck((Boolean) response.get("needItemCheck"))
                    .paymentMerchant(event.getPaymentMerchant())
                    .storeName(event.getStoreName())                  // ✅ 매장명
                    .receiptUploaded(event.isReceiptUploaded())       // ✅ 영수증 업로드 여부
                    .paymentStatus(event.getPaymentStatus())
                    .createdAt(event.getCreatedAt())
                    .orders(convertOrderDetails(event.getOrders()))
                    .paymentCategory((String) response.get("paymentCategory"))
                    .categoryAmount(categoryAmount)
                    .build();

            log.info("💾 Saving payment with category: {}", history.getPaymentCategory());
            paymentHistoryRepository.save(history);
            log.info("✅ Saved PaymentHistory to Elasticsearch with ID: {}", history.getTransactionId());
        } catch (Exception e) {
            log.error("Error processing payment with store name {}: {}", event.getStoreName(), e.getMessage());
            throw e;  // 상위(PaymentEventConsumer)에서 처리하도록 예외 전파
        }
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
