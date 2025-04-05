package com.locket.elasticsearch.service.payment;

import com.locket.elasticsearch.common.dto.DateRange;
import com.locket.elasticsearch.common.util.DateTimeUtil;
import com.locket.elasticsearch.domain.payment.dto.CalendarPaymentDto;
import com.locket.elasticsearch.domain.payment.dto.DayPaymentDto;
import com.locket.elasticsearch.domain.payment.dto.MonthPaymentDto;
import com.locket.elasticsearch.domain.payment.dto.ReceiptPaymentDto;
import com.locket.elasticsearch.domain.payment.dto.CalendarPaymentDto.DailySpending;
import com.locket.elasticsearch.domain.payment.entity.PaymentHistory;
import com.locket.elasticsearch.domain.payment.repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ArrayList;


@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentQueryService {

    private final PaymentHistoryRepository paymentHistoryRepository;

    /**
     * 공통 메서드: 사용자 ID, 연도, 월 기준으로 결제 내역 조회
     */
    private List<PaymentHistory> getPaymentsInMonth(long userId, int year, int month) {
        DateRange range = DateTimeUtil.getMonthRangeUtc(year, month);
        return paymentHistoryRepository.findByBuyerIdAndYearAndMonth(userId, year, month);
    }

    /**
     * 월별 일자별 소비 내역 + 총합
     */
    public CalendarPaymentDto getCalendarPaymentData(long userId, int year, int month) {
        List<PaymentHistory> payments = getPaymentsInMonth(userId, year, month);

        Map<String, Integer> dailyTotals = new TreeMap<>(); // TreeMap으로 자동 정렬
        int monthlyTotal = 0;

        for (PaymentHistory payment : payments) {
            String date = payment.getCreatedAt().toLocalDate().toString(); // yyyy-MM-dd
            int amount = payment.getTotalAmount().intValue();

            dailyTotals.merge(date, amount, Integer::sum);
            monthlyTotal += amount;
        }

        List<DailySpending> dailySpending = dailyTotals.entrySet().stream()
                .map(entry -> DailySpending.builder()
                        .date(entry.getKey())
                        .amount(entry.getValue())
                        .build())
                .collect(Collectors.toList());

        return CalendarPaymentDto.builder()
                .monthlyTotal(monthlyTotal)
                .dailySpending(dailySpending)
                .build();
    }

    /**
     * 월별 상세 소비 내역 (카드/가맹점/카테고리 중심)
     */
    public List<MonthPaymentDto> getMonthPaymentData(long userId, int year, int month) {
        return getPaymentsInMonth(userId, year, month).stream()
                .sorted(Comparator.comparing(PaymentHistory::getCreatedAt).reversed())
                .map(p -> MonthPaymentDto.builder()
                        .id(p.getTransactionId())
                        .day(p.getDay())
                        .paymentCategory(p.getPaymentCategory())
                        .cardName(p.getCardName())
                        .storeName(p.getStoreName())
                        .totalAmount(p.getTotalAmount())
                        .year(p.getYear())
                        .month(p.getMonth())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 일별 상세 소비 내역 (카드/가맹점/카테고리 중심)
     */
    public List<DayPaymentDto> getDayPaymentData(long userId, int year, int month, int day) {
        List<PaymentHistory> payments = paymentHistoryRepository.findByBuyerIdAndYearAndMonthAndDay(userId, year, month, day);

        return payments.stream()
                .sorted(Comparator.comparing(PaymentHistory::getCreatedAt).reversed())
                .map(p -> DayPaymentDto.builder()
                        .id(p.getTransactionId())
                        .day(p.getDay())
                        .paymentCategory(p.getPaymentCategory())
                        .cardName(p.getCardName())
                        .storeName(p.getStoreName())
                        .totalAmount(p.getTotalAmount())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 단순 조회용: 월별 결제 내역 반환
     */
    public List<PaymentHistory> findByUserAndMonth(long userId, int year, int month) {
        return getPaymentsInMonth(userId, year, month);
    }

    /**
     * 영수증 등록이 가능한 결제 내역 조회.
     * 결제 상태가 'SUCCESS'이고 영수증이 등록되지 않은(receiptUploaded=false) 내역만 반환
     * @throws IllegalArgumentException 유효하지 않은 userId가 입력된 경우
     */
    public ReceiptPaymentDto getReceiptRegisterablePayments(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId는 null일 수 없습니다.");
        }

        List<PaymentHistory> payments = paymentHistoryRepository.findByBuyerIdAndPaymentStatusAndReceiptUploaded(
                userId,
                "SUCCESS",
                false
        );

        List<ReceiptPaymentDto.Receipt> receipts = payments.stream()
                .map(payment -> ReceiptPaymentDto.Receipt.builder()
                        .transactionId(payment.getTransactionId())
                        .storeName(payment.getStoreName())
                        .paymentCategory(payment.getPaymentCategory())
                        .cardName(payment.getCardName())
                        .paymentDate(String.format("%d.%02d.%02d",
                                payment.getYear(),
                                payment.getMonth(),
                                payment.getDay()))
                        .amount(payment.getTotalAmount().intValue())
                        .build())
                .collect(Collectors.toList());

        return ReceiptPaymentDto.builder()
                .receipts(receipts)
                .build();
    }


    private Map<String, Integer> calculateCategoryAmounts(List<PaymentHistory> payments) {
        Map<String, Integer> totalCategoryAmount = new HashMap<>();

        for (PaymentHistory payment : payments) {
            String category = payment.getPaymentCategory();
            int amount = payment.getTotalAmount().intValue();

            // 기존 금액에 현재 금액을 더함
            totalCategoryAmount.merge(category, amount, Integer::sum);

            log.info("Adding payment: category={}, amount={}, running total={}",
                    category, amount, totalCategoryAmount.get(category));
        }

        log.info("Final category totals: {}", totalCategoryAmount);
        return totalCategoryAmount;
    }

    /**
     * 카테고리별 지출 합계를 위한 결제 내역 조회
     */
    private List<PaymentHistory> getAllPaymentsInMonth(long userId, int year, int month) {
        // 1. year, month 기반 조회
        List<PaymentHistory> yearMonthResults =
                paymentHistoryRepository.findByBuyerIdAndYearAndMonthCustomQuery(userId, year, month);
        log.info("Year/Month query results: {}", yearMonthResults.size());

        // 2. createdAt 기반 조회
        DateRange range = DateTimeUtil.getMonthRangeUtc(year, month);
        List<PaymentHistory> dateRangeResults =
                paymentHistoryRepository.findByBuyerIdAndCreatedAtBetween(userId, range.getStart(), range.getEnd());
        log.info("Date range query results: {}", dateRangeResults.size());

        // 3. 두 결과를 합치고 중복 제거
        List<PaymentHistory> allResults = new ArrayList<>();
        allResults.addAll(yearMonthResults);
        allResults.addAll(dateRangeResults);

        return allResults.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                PaymentHistory::getTransactionId,
                                payment -> payment,
                                (p1, p2) -> p1
                        ),
                        map -> new ArrayList<>(map.values())
                ));
    }

    /**
     * 월별 카테고리별 지출 합계 조회
     */
    public Map<String, Integer> getCategoryAmountsByMonth(long userId, int year, int month) {
        List<PaymentHistory> payments = getAllPaymentsInMonth(userId, year, month);
        log.info("Found {} total payments for user {} in {}-{}",
                payments.size(), userId, year, month);

        Map<String, Integer> categoryAmounts = calculateCategoryAmounts(payments);
        log.info("Category amounts for user {} in {}-{}: {}",
                userId, year, month, categoryAmounts);

        return categoryAmounts;
    }

    // 한 카드의 이번달 총 이용 금액 조회
    public BigDecimal getMonthlyTotalByCard(Long userId, Integer cardId, int year, int month) {
        List<PaymentHistory> payments = paymentHistoryRepository.findByBuyerIdAndCardIdAndYearAndMonth(userId, cardId, year, month);

        return payments.stream()
                .map(PaymentHistory::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
