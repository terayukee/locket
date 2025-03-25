package com.locket.payment.controller.pay;

import com.locket.payment.domain.pay.dto.CardInfoDto;
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
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.locket.common.jwt.JwtUtil;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Tag(name = "결제 API", description = "결제 관련 API")
public class PayController {

    private final PayService payService;
    private final JwtUtil jwtUtil;

    @PostMapping("/nfc")
    @Operation(summary = "결제", description = "결제를 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
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
                                            "  \"cardNumber\": \"1234123412341234\",\n" +
                                            "  \"buyerId\": 1,\n" +
                                            "  \"sellerId\": 2,\n" +
                                            "  \"paymentCategory\": \"카페\",\n" +
                                            "  \"paymentMerchant\": \"아메리카노\",\n" +
                                            "  \"amount\": 2000,\n" +
                                            "  \"storeName\": \"메가커피 구미인동점\"\n" +
                                            "}"
                            )
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody PaymentRequest request
    )  {
        return payService.processPayment(request);
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
                                            "  \"cardNumber\": \"1234123412341234\",\n" +
                                            "  \"amount\": 2000\n" +
                                            "}"
                            )
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody PaymentRequest request
    ) {
        return payService.validateCardAndBalance(request.getCardNumber(), request.getAmount());
    }


    @PostMapping("/my-cards")
    @Operation(summary = "내 카드 목록 조회", description = "JWT를 기반으로 현재 사용자 ID에 연결된 카드 목록을 조회합니다.")
    public ResponseEntity<List<CardInfoDto>> getMyCards(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        int userId = 0;
        userId = jwtUtil.extractUserId(token);
        return ResponseEntity.ok(payService.getCardsByUserId(userId));
    }

}
