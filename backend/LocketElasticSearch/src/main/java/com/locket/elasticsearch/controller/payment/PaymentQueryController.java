package com.locket.elasticsearch.controller.payment;

import com.locket.elasticsearch.domain.payment.dto.CalendarPaymentDto;
import com.locket.elasticsearch.domain.payment.dto.DayPaymentDto;
import com.locket.elasticsearch.domain.payment.dto.MonthPaymentDto;
import com.locket.elasticsearch.domain.payment.dto.ReceiptPaymentDto;
import com.locket.elasticsearch.domain.payment.entity.PaymentHistory;
import com.locket.elasticsearch.service.payment.PaymentQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Locket ElasticSearch Service")
@Slf4j
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentQueryController {

    private final PaymentQueryService paymentQueryService;

    @GetMapping("/available/{userId}")
    @Operation(
            summary = "영수증 등록 가능한 전체 결제 내역 조회",
            description = "품목 카테고리 분류가 필요하며, 아직 영수증이 등록되지 않은 결제 내역을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<List<ReceiptPaymentDto>> getReceiptRegisterablePayments(
            @PathVariable(name = "userId") long userId
    ) {
        try {
            List<ReceiptPaymentDto> payments = paymentQueryService.getReceiptRegisterablePayments(userId);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            log.error("Failed to get receipt registerable payments for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("영수증 등록 가능한 결제 내역 조회 중 오류가 발생했습니다.");
        }
    }

    @GetMapping("/history")
    @Operation(
            summary = "월 단위 결제 내역 전체 조회",
            description = "지정된 연/월에 대한 결제 내역 전체(PaymentHistory)를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> getMonthlyPaymentHistory(
            @RequestParam long userId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        List<PaymentHistory> historyList = paymentQueryService.findByUserAndMonth(userId, year, month);
        return ResponseEntity.ok(historyList);
    }


    @GetMapping("/calendar")
    @Operation(summary = "월 단위 결제 내역 리스트 조회 (캘린더용)", description = "지정된 연/월에 대한 전체 지출 합계 및 일자별 지출 금액 리스트를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<CalendarPaymentDto> getCalendarPayments(
            @RequestParam long userId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        CalendarPaymentDto result = paymentQueryService.getCalendarPaymentData(userId, year, month);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/month")
    @Operation(summary = "월 단위 결제 내역 리스트 조회 (내역용)", description = "지정된 연/월의 결제 내역에서 paymentCategory, cardName, storeName, totalAmount, year, month 필드를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<List<MonthPaymentDto>> getMonthPayments(
            @RequestParam long userId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        List<MonthPaymentDto> result = paymentQueryService.getMonthPaymentData(userId, year, month);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/day")
    @Operation(summary = "일 단위 결제 내역 리스트 조회", description = "지정된 연/월/일의 결제 내역에서 paymentCategory, cardName, storeName, totalAmount 필드를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<List<DayPaymentDto>> getDayPayments(
            @RequestParam long userId,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam int day
    ) {
        List<DayPaymentDto> result = paymentQueryService.getDayPaymentData(userId, year, month, day);
        return ResponseEntity.ok(result);

    }


}
