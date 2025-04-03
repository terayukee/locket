package com.locket.payment.controller.pay;

import com.locket.payment.domain.pay.dto.CardInfoDto;
import com.locket.payment.domain.pay.dto.PaymentPasswordRequest;
import com.locket.payment.domain.pay.dto.PaymentRequest;
import com.locket.payment.domain.pay.dto.PaymentResponse;
import com.locket.payment.service.pay.PayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@Tag(name = "결제 API", description = "결제 관련 API")
public class PayController {

    private final PayService payService;

    @PostMapping("/nfc")
    @Operation(summary = "결제", description = "결제를 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "409", description = "중복 결제 요청"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<PaymentResponse> processPayment(
            @RequestBody(
                    description = "결제 요청 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "결제 예시",
                                    summary = "기본 결제 요청 예시",
                                    value = "{\n" +
                                            "  \"cardId\": 1,\n" +
                                            "  \"sellerId\": 2,\n" +
                                            "  \"paymentCategory\": \"카페\",\n" +
                                            "  \"paymentMerchant\": \"아메리카노\",\n" +
                                            "  \"amount\": 2000,\n" +
                                            "  \"storeName\": \"메가커피 구미인동점\",\n" +
                                            "  \"paymentKey\": \"123e4567-e89b-12d3-a456-426614174000\"\n" +
                                            "}"
                            )
                    )
            )
            @RequestHeader("X-User-Id") Long userId,
            @org.springframework.web.bind.annotation.RequestBody PaymentRequest request
    ) {
        return payService.processPayment(request, userId);
    }

    @PostMapping("/validate-card")
    @Operation(summary = "카드 유효성 및 잔액 확인", description = "카드번호와 결제 금액을 받아 유효성과 잔액을 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검증 성공"),
            @ApiResponse(responseCode = "400", description = "검증 실패")
    })
    public ResponseEntity<?> validateCard(
            @RequestBody(
                    description = "카드 유효성 요청 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "카드 검증 예시",
                                    summary = "기본 카드 검증 요청 예시",
                                    value = "{\n" +
                                            "  \"cardId\": 1,\n" +
                                            "  \"amount\": 2000\n" +
                                            "}"
                            )
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody PaymentRequest request
    ) {
        return payService.validateCardAndBalance(request.getCardId(), request.getAmount());
    }

    @GetMapping("/cards")
    @Operation(summary = "내 카드 목록 조회", description = "사용자 ID를 기반으로 등록된 카드 목록을 조회합니다.")
    public ResponseEntity<?> getMyCards(
            @RequestHeader("X-User-Id") Long userId
    ) {
        try {
            List<CardInfoDto> cards = payService.getCardsByUserId(userId);
            return ResponseEntity.ok(cards);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of(
                    "status", 404,
                    "message", "NOT_FOUND",
                    "error", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "status", 500,
                    "message", "INTERNAL_ERROR",
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/auth-info/fingerprint")
    @Operation(summary = "지문 등록 여부 조회", description = "사용자 ID를 기반으로 Redis에서 지문 등록 여부를 조회합니다.")
    public ResponseEntity<?> checkFingerprintRegistered(
            @RequestHeader("X-User-Id") Long userId
    ) {
        try {
            boolean registered = payService.getFingerprintRegisteredFromRedis(userId);
            return ResponseEntity.ok(Map.of(
                    "userId", userId,
                    "fingerprintRegistered", registered
            ));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of(
                    "status", 404,
                    "message", "NOT_FOUND",
                    "error", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "status", 500,
                    "message", "INTERNAL_ERROR",
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/auth/verify-password")
    @Operation(summary = "간편 비밀번호 검증", description = "사용자 ID와 입력된 간편 비밀번호를 검증합니다.")
    public ResponseEntity<?> verifyPaymentPassword(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody PaymentPasswordRequest passwordRequest
    ) {
        try {
            boolean isValid = payService.verifyPaymentPassword(userId, passwordRequest.getPaymentPassword());
            return ResponseEntity.ok(Map.of(
                    "userId", userId,
                    "valid", isValid
            ));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of(
                    "status", 404,
                    "message", "NOT_FOUND",
                    "error", e.getMessage()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of(
                    "status", 400,
                    "message", "BAD_REQUEST",
                    "error", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "status", 500,
                    "message", "INTERNAL_ERROR",
                    "error", e.getMessage()
            ));
        }
    }
}