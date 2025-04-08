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

        // 기존 카테고리 확인
        String paymentCategory = event.getPaymentCategory() != null ?
                event.getPaymentCategory() : DEFAULT_CATEGORY;
        boolean needItemCheck = DEFAULT_NEED_ITEM_CHECK;
        Map<String, Integer> categoryAmount = new HashMap<>();

        // 카테고리가 기본값일 때만 AI 서비스 호출
        if (DEFAULT_CATEGORY.equals(paymentCategory)) {
            try {
                log.info("카테고리 분류 요청 - 상호명 : {}", event.getStoreName());

                Map<String, Object> response = webClient.post()
                        .uri("/api/ai/category/classify")
                        .bodyValue(Map.of("storeName", event.getStoreName()))
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                        .block();

                if (response != null) {
                    Object categoryObj = response.get("paymentCategory");
                    Object itemCheckObj = response.get("needItemCheck");

                    if (categoryObj instanceof String categoryStr && !categoryStr.isBlank()) {
                        paymentCategory = categoryStr;

                        if (DEFAULT_CATEGORY.equals(categoryStr)) {
                            log.warn("AI 응답에서 '기타' 카테고리 반환됨 - 상호명: {}", event.getStoreName());
                        }

                        if (itemCheckObj instanceof Boolean) {
                            needItemCheck = (Boolean) itemCheckObj;
                        } else {
                            log.warn("needItemCheck 값이 boolean이 아님 - 기본값 사용(true)");
                        }

                        log.info("카테고리 분류 성공 - 카테고리: {}, 품목확인필요: {}", paymentCategory, needItemCheck);
                    } else {
                        log.warn("AI 응답에서 유효하지 않은 paymentCategory - 기본값 '기타' 사용됨, 응답 내용: {}", response);
                    }
                } else {
                    log.warn("AI 응답이 null - 기본 카테고리 사용, storeName: {}", event.getStoreName());
                }
            } catch (Exception e) {
                log.warn("카테고리 분류 서비스 호출 실패, 기본 카테고리 사용 - 상호명: {}, 오류: {}",
                        event.getStoreName(), e.getMessage());
                // 기본값 유지
            }
        } else {
            log.info("기존 카테고리 사용: {}", paymentCategory);
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
