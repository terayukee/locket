package com.locket.payment.controller.pay;

import com.locket.payment.domain.pay.dto.QrPaymentRequest;
import com.locket.payment.domain.pay.dto.QrPaymentResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Tag(name = "결제 API", description = "QR 코드 결제 관련 API")
public class PayController {

    private final PayService payService;

    @PostMapping("/qr")
    @Operation(summary = "QR 코드 결제", description = "QR 코드를 이용하여 결제를 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<QrPaymentResponse> processQrPayment(
            @RequestBody(
                    description = "QR 결제 요청 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "QR 결제 예시",
                                    summary = "기본 결제 요청 예시",
                                    value = "{\n" +
                                            "  \"cardNumber\": \"1234123412341234\",\n" +
                                            "  \"buyerId\": 1,\n" +
                                            "  \"sellerId\": 2,\n" +
                                            "  \"paymentCategory\": \"카페\",\n" +
                                            "  \"paymentMerchant\": \"아메리카노\",\n" +
                                            "  \"amount\": 2000\n" +
                                            "}"
                            )
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody QrPaymentRequest request
    )  {
        return payService.processQrPayment(request);
    }

    @GetMapping("/test")
    @Operation(summary = "결제 서비스 테스트", description = "결제 서비스가 정상 작동하는지 테스트합니다.")
    @ApiResponse(responseCode = "200", description = "서비스 정상 작동")
    public ResponseEntity<QrPaymentResponse> testPayment() {
        QrPaymentResponse response = QrPaymentResponse.builder()
                .transactionId("test-transaction-id")
                .status("SUCCESS")
                .message("Payment Service is Running!")
                .build();
        return ResponseEntity.ok(response);
    }
}
