package com.locket.elasticsearch.service.payment;

import com.locket.elasticsearch.domain.payment.entity.PaymentHistory;
import com.locket.elasticsearch.domain.payment.entity.PaymentHistory.OrderDetail;
import com.locket.elasticsearch.domain.payment.repository.PaymentHistoryRepository;
import com.locket.kafka.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.locket.elasticsearch.dto.CategoryResponse;
import org.springframework.web.reactive.function.client.WebClient;
import com.locket.elasticsearch.dto.StoreRequest;

import java.util.List;
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
            // FastAPI로 store_name을 POST Body로 전달
            StoreRequest requestBody = new StoreRequest(event.getStoreName());

            CategoryResponse categoryResponse = webClient.post()
                .uri("/api/category/classify")
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .map(body -> new RuntimeException("API call failed: " + body))
                )
                .bodyToMono(CategoryResponse.class)
                .block();

            // DTO -> Elasticsearch Entity로 변환
            PaymentHistory history = PaymentHistory.builder()
                    .transactionId(event.getTransactionId())
                    .buyerId(event.getBuyerId())
                    .sellerId(event.getSellerId())
                    .userJob(event.getUserJob())
                    .birthDate(event.getBirthDate())
                    .totalAmount(event.getTotalAmount())
                    .currency(event.getCurrency())
                    .paymentCategory(categoryResponse.getCategory())  // API 응답으로 받은 카테고리 사용
                    //.paymentCategory(event.getPaymentCategory())
                    .needsItemCheck(categoryResponse.isNeedsItemCheck())
                    .paymentMerchant(event.getPaymentMerchant())
                    .storeName(event.getStoreName())                  // ✅ 매장명
                    .receiptUploaded(event.isReceiptUploaded())       // ✅ 영수증 업로드 여부
                    .paymentStatus(event.getPaymentStatus())
                    .createdAt(event.getCreatedAt())
                    .orders(convertOrderDetails(event.getOrders()))
                    .build();

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
