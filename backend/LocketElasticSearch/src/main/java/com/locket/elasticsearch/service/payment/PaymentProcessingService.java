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
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProcessingService {

    private static final String DEFAULT_CATEGORY = "기타";
    private static final boolean DEFAULT_NEED_ITEM_CHECK = true;

    private final PaymentHistoryRepository paymentHistoryRepository;
    private final WebClient webClient;

    public void processPaymentSuccess(PaymentSuccessEvent event) {
        log.info("✅ Processing Payment Success Event: {}", event);

        String paymentCategory = DEFAULT_CATEGORY;
        boolean needItemCheck = DEFAULT_NEED_ITEM_CHECK;
        Map<String, Integer> categoryAmount = new HashMap<>();

        // AI 서비스 호출 부분 - 실패해도 기본값 사용
        try {
            log.info("카테고리 분류 요청 - 상호명 : {}", event.getStoreName());
            Map<String, Object> response = webClient.post()
                    .uri("/api/ai/category/classify")
                    .bodyValue(Map.of("storeName", event.getStoreName()))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
            if (response != null && response.get("paymentCategory") != null) {
                paymentCategory = (String) response.get("paymentCategory");
                needItemCheck = Boolean.TRUE.equals(response.get("needItemCheck"));
                log.info("카테고리 분류 성공 - 카테고리: {}, 품목확인필요: {}",
                        paymentCategory, needItemCheck);
            } else {
                log.warn("카테고리 분류 실패, 기본 카테고리 사용");
            }
        } catch (Exception e) {
            log.warn("⚠️ 카테고리 분류 서비스 호출 실패, 기본 카테고리 사용 - 상호명: {}, 오류: {}",
                    event.getStoreName(), e.getMessage());
            // 기본값 사용 (이미 설정되어 있음)
        }


        // Elasticsearch 저장 부분 - 실패 시 예외 전파
        try {
            // categoryAmount 설정
            categoryAmount.put(paymentCategory, event.getTotalAmount().intValue());

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
                    .paymentCategory(paymentCategory)
                    .needItemCheck(needItemCheck)
//                    .needItemCheck((Boolean) response.get("needItemCheck"))
                    .paymentMerchant(event.getPaymentMerchant())
                    .storeName(event.getStoreName())                  // ✅ 매장명
                    .receiptUploaded(event.isReceiptUploaded())       // ✅ 영수증 업로드 여부
                    .paymentStatus(event.getPaymentStatus())
                    .createdAt(event.getCreatedAt())
                    .year(event.getYear())
                    .month(event.getMonth())
                    .day(event.getDay())
                    .orders(convertOrderDetails(event.getOrders()))
                    .cardId(event.getCardId())
                    .cardName(event.getCardName())
                    .categoryAmount(categoryAmount)
                    .build();

            log.info("💾 Saving payment with category: {}", history.getPaymentCategory());
            paymentHistoryRepository.save(history);
            log.info("✅ Saved PaymentHistory to Elasticsearch with ID: {}", history);
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
