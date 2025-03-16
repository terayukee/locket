package com.locket.payment.controller.pay;

import com.locket.payment.service.pay.PayService;
import com.locket.payment.domain.pay.dto.QrPaymentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ResponseEntity<Map<String, Object>> processQrPayment(@RequestBody QrPaymentRequest request) {
        return payService.processQrPayment(request);
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testPayment() {
        Map<String, Object> response = Map.of("message", "Payment Service is Running!");
        return ResponseEntity.ok(response);
    }
}
