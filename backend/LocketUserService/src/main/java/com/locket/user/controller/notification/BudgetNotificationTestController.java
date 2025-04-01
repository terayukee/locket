package com.locket.user.controller.notification;

import com.locket.kafka.event.PaymentSuccessEvent;
import com.locket.user.domain.notification.dto.BudgetTestRequest;
import com.locket.user.service.notification.BudgetNotificationService;
import com.locket.user.service.notification.BudgetNotificationService.NotificationResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/notification/test")
@Tag(name = "🔔 FCM 알림 테스트", description = "FCM 예산 초과 알림 테스트용 API")
public class BudgetNotificationTestController {

    private final BudgetNotificationService budgetNotificationService;

    @PostMapping("/budget/over-limit")
    @Operation(
            summary = "예산 초과 알림 테스트",
            description = "예산 초과 조건을 만족하는지 확인하고 FCM 알림을 테스트합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "테스트 요청 바디",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "예산 초과 알림 요청 예시",
                                    summary = "기본 테스트",
                                    value = "{\n" +
                                            "  \"userId\": 1,\n" +
                                            "  \"amount\": 1000000,\n" +
                                            "  \"category\": \"식비\",\n" +
                                            "  \"merchant\": \"참치김밥\",\n" +
                                            "  \"store\": \"김밥천국 강남본점\"\n" +
                                            "}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "테스트 결과 반환", content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "알림 성공", value = "{ \"status\": \"SUCCESS\", \"message\": \"✅ 알림 전송 및 저장 완료 (예산 사용률 92%)\" }"),
                                    @ExampleObject(name = "알림 조건 불충족", value = "{ \"status\": \"NO_CONDITION\", \"message\": \"⛔ 알림 조건 미충족 (예산 사용률 45%)\" }"),
                                    @ExampleObject(name = "중복 알림 방지", value = "{ \"status\": \"ALREADY_SENT\", \"message\": \"⛔ 이미 동일한 예산 초과 알림이 전송됨\" }"),
                                    @ExampleObject(name = "목표 없음", value = "{ \"status\": \"NO_GOAL\", \"message\": \"❌ 예산 목표가 존재하지 않습니다.\" }"),
                                    @ExampleObject(name = "사용자 정보 없음", value = "{ \"status\": \"NO_USER\", \"message\": \"❗ 사용자 정보 또는 FCM 토큰 없음\" }")
                            }
                    ))
            }
    )
    public ResponseEntity<Map<String, String>> testBudgetAlert(@org.springframework.web.bind.annotation.RequestBody BudgetTestRequest request) {
        log.info("🔍 요청 값: userId={}, amount={}", request.getUserId(), request.getAmount());

        PaymentSuccessEvent event = PaymentSuccessEvent.builder()
                .buyerId(request.getUserId())
                .sellerId(999)
                .userJob("직장인")
                .birthDate(1990)
                .totalAmount(BigDecimal.valueOf(request.getAmount()))
                .currency("KRW")
                .cardId(1)
                .cardName("테스트카드")
                .paymentMerchant(request.getMerchant())
                .paymentCategory(request.getCategory())
                .storeName(request.getStore())
                .receiptUploaded(false)
                .paymentStatus("SUCCESS")
                .createdAt(OffsetDateTime.now())
                .year(OffsetDateTime.now().getYear())
                .month(OffsetDateTime.now().getMonthValue())
                .day(OffsetDateTime.now().getDayOfMonth())
                .orders(Collections.emptyList())
                .transactionId("TEST-" + System.currentTimeMillis())
                .build();

        NotificationResult result = budgetNotificationService.testHandleBudgetNotification(event);

        Map<String, String> response = new HashMap<>();
        response.put("status", result.getStatus());
        response.put("message", result.getMessage());

        return ResponseEntity.ok(response);
    }
}
