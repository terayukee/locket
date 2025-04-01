package com.locket.elasticsearch.controller.payment;

import com.locket.elasticsearch.domain.payment.dto.ReceiptUpdateRequest;
import com.locket.elasticsearch.service.payment.ReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/receipt")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    @PostMapping("/save/{transactionId}")
    @Operation(summary = "영수증 OCR 결과 저장", description = "영수증 OCR 처리 결과를 저장하고 receiptUploaded 상태를 true로 변경합니다.")
    public ResponseEntity<?> saveReceiptInfo(
            @PathVariable String transactionId,
            @RequestBody ReceiptUpdateRequest request
    ) {
        try {
            if (!transactionId.equals(request.getTransactionId())) {
                return ResponseEntity.badRequest().body("transactionId가 일치하지 않습니다.");
            }
            receiptService.updateReceiptInfo(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("영수증 정보 저장 실패: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}