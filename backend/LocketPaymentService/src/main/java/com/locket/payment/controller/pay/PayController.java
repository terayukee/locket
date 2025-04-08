package com.locket.payment.controller.pay;

import com.locket.payment.domain.pay.dto.*;
import com.locket.payment.security.RequiresUser;
import com.locket.payment.service.pay.PayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@Tag(name = "💳 결제 API", description = "결제 및 카드 관련 기능들을 제공합니다.")
@RequestMapping("/api/payment")
public class PayController {

    private final PayService payService;

    @RequiresUser(ownerOnly = true)
    @PostMapping("/nfc")
    @Operation(
            summary = "🧾 결제 처리",
            description = "NFC 결제 요청을 처리하고 결제 트랜잭션을 생성합니다.",
            requestBody = @RequestBody(
                    description = "💳 결제 요청 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaymentRequest.class),
                            examples = @ExampleObject(
                                    name = "결제 예시",
                                    summary = "기본 결제 요청 예시",
                                    value = """
                                    {
                                      "cardId": 1,
                                      "sellerId": 2,
                                      "paymentCategory": "카페",
                                      "paymentMerchant": "아메리카노",
                                      "amount": 2000,
                                      "storeName": "메가커피 구미인동점",
                                      "paymentKey": "123e4567-e89b-12d3-a456-426614174000"
                                    }
                                    """
                            )
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "✅ 결제 성공"),
            @ApiResponse(responseCode = "400", description = "❌ 잘못된 요청"),
            @ApiResponse(responseCode = "409", description = "⚠️ 중복 결제 요청"),
            @ApiResponse(responseCode = "500", description = "🚨 서버 내부 오류")
    })
    public ResponseEntity<PaymentResponse> processPayment(
            @org.springframework.web.bind.annotation.RequestBody PaymentRequest request,
            @RequestHeader("Authorization") String token
    ) {
        return payService.processPayment(request);
    }

    @PostMapping("/validate-card")
    @Operation(
            summary = "🔍 카드 유효성 및 잔액 확인",
            description = "입력된 카드 ID와 결제 금액을 기반으로 유효성과 잔액을 검증합니다.",
            requestBody = @RequestBody(
                    description = "💳 카드 정보 및 금액",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaymentRequest.class),
                            examples = @ExampleObject(
                                    name = "카드 검증 예시",
                                    summary = "기본 카드 검증 요청 예시",
                                    value = """
                                    {
                                      "cardId": 1,
                                      "amount": 2000
                                    }
                                    """
                            )
                    )
            )
    )
    public ResponseEntity<?> validateCard(
            @org.springframework.web.bind.annotation.RequestBody PaymentRequest request,
            @RequestHeader("Authorization") String token
    ) {
        return payService.validateCardAndBalance(request.getCardId(), request.getAmount());
    }

    @RequiresUser(ownerOnly = true)
    @GetMapping("/cards")
    @Operation(
            summary = "💳 내 카드 목록 조회",
            description = """
        ✅ 사용자 ID를 기반으로 등록된 모든 카드를 조회합니다.<br>
        💰 각 카드의 이번 달 총 결제 금액(`monthlyUsage`)과 카드 혜택(`benefits`)도 함께 제공합니다.<br><br>
        📌 `Authorization` 헤더를 통해 JWT 토큰을 전달해야 합니다.
        """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "🟢 조회 성공"),
            @ApiResponse(responseCode = "404", description = "🔴 사용자의 카드가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "🟠 서버 내부 오류")
    })
    public ResponseEntity<?> getMyCards(
            @RequestHeader("Authorization") String token,
            @RequestParam long userId
    ) {
        try {
            List<CardInfoDto> cards = payService.getCardsWithMonthlyUsage(userId);
            return ResponseEntity.ok(Map.of(
                    "cardList", cards,
                    "count", cards.size()
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

    @RequiresUser(ownerOnly = true)
    @GetMapping("/auth-info/fingerprint")
    @Operation(
            summary = "🧬 지문 등록 여부 조회",
            description = "Redis에서 사용자의 지문 등록 여부를 조회합니다."
    )
    public ResponseEntity<?> checkFingerprintRegistered(
            @RequestHeader("Authorization") String token,
            @RequestParam long userId
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

    @RequiresUser(ownerOnly = true)
    @PostMapping("/auth/verify-password")
    @Operation(
            summary = "🔐 간편 결제 비밀번호 검증",
            description = "Redis에 저장된 사용자 결제 비밀번호와 입력값을 비교하여 검증합니다.",
            requestBody = @RequestBody(
                    description = "사용자 ID 및 입력된 결제 비밀번호",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaymentPasswordRequest.class),
                            examples = @ExampleObject(
                                    name = "비밀번호 검증 예시",
                                    value = """
                                    {
                                      "userId": 104,
                                      "paymentPassword": 111111
                                    }
                                    """
                            )
                    )
            )
    )
    public ResponseEntity<?> verifyPaymentPassword(
            @RequestHeader("Authorization") String token,
            @org.springframework.web.bind.annotation.RequestBody PaymentPasswordRequest passwordRequest
    ) {
        try {
            boolean isValid = payService.verifyPaymentPassword(passwordRequest);
            return ResponseEntity.ok(Map.of(
                    "userId", passwordRequest.getUserId(),
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

    @RequiresUser(ownerOnly = true)
    @GetMapping("/monthly-total")
    @Operation(
            summary = "📊 월간 총 결제 금액 조회",
            description = "사용자 ID, 연도, 월을 기반으로 해당 월의 총 결제 금액을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "🟢 조회 성공"),
            @ApiResponse(responseCode = "500", description = "🟠 서버 내부 오류")
    })
    public ResponseEntity<?> getMonthlyTotalAmount(
            @RequestParam long userId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        try {
            BigDecimal total = payService.getMonthlyTotalAmount(userId, year, month);
            return ResponseEntity.ok(Map.of(
                    "userId", userId,
                    "year", year,
                    "month", month,
                    "totalAmount", total
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
