package com.locket.elasticsearch.service.payment;

import com.locket.elasticsearch.common.dto.DateRange;
import com.locket.elasticsearch.common.util.DateTimeUtil;
import com.locket.elasticsearch.domain.payment.dto.CalendarPaymentDto;
import com.locket.elasticsearch.domain.payment.dto.SummaryPaymentDto;
import com.locket.elasticsearch.domain.payment.dto.CalendarPaymentDto.DailySpending;
import com.locket.elasticsearch.domain.payment.entity.PaymentHistory;
import com.locket.elasticsearch.domain.payment.repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.Comparator;


@Service
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
    public List<SummaryPaymentDto> getSummaryPaymentData(long userId, int year, int month) {
        return getPaymentsInMonth(userId, year, month).stream()
                .sorted(Comparator.comparing(PaymentHistory::getCreatedAt).reversed())
                .map(p -> SummaryPaymentDto.builder()
                        .paymentCategory(p.getPaymentCategory())
                        .paymentMerchant(p.getPaymentMerchant())
                        .cardId(p.getCardId())
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
}
