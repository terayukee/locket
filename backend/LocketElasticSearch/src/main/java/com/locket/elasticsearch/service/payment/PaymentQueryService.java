package com.locket.elasticsearch.service.payment;

import com.locket.elasticsearch.common.dto.DateRange;
import com.locket.elasticsearch.common.util.DateTimeUtil;
import com.locket.elasticsearch.domain.payment.dto.CalendarPaymentDto;
import com.locket.elasticsearch.domain.payment.dto.CalendarPaymentDto.DailySpending;
import com.locket.elasticsearch.domain.payment.dto.SummaryPaymentDto;
import com.locket.elasticsearch.domain.payment.entity.PaymentHistory;
import com.locket.elasticsearch.domain.payment.repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentQueryService {

    private final PaymentHistoryRepository paymentHistoryRepository;

    public List<PaymentHistory> findByUserAndMonth(int userId, int year, int month) {
        DateRange range = DateTimeUtil.getMonthRangeUtc(year, month);
        return paymentHistoryRepository.findByBuyerIdAndCreatedAtBetween(
                userId, range.getStart(), range.getEnd()
        );
    }

    public CalendarPaymentDto getCalendarPaymentData(int userId, int year, int month) {
        DateRange range = DateTimeUtil.getMonthRangeUtc(year, month);
        List<PaymentHistory> payments = paymentHistoryRepository.findByBuyerIdAndCreatedAtBetween(
                userId, range.getStart(), range.getEnd()
        );

        Map<String, Integer> dailyTotals = new HashMap<>();
        int monthlyTotal = 0;

        for (PaymentHistory payment : payments) {
            String date = payment.getCreatedAt().toLocalDate().toString(); // yyyy-MM-dd
            int amount = payment.getTotalAmount().intValue();

            dailyTotals.put(date, dailyTotals.getOrDefault(date, 0) + amount);
            monthlyTotal += amount;
        }

        List<DailySpending> dailySpending = dailyTotals.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
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

    public List<SummaryPaymentDto> getSummaryPaymentData(int userId, int year, int month) {
        DateRange range = DateTimeUtil.getMonthRangeUtc(year, month);
        List<PaymentHistory> payments = paymentHistoryRepository.findByBuyerIdAndCreatedAtBetween(
                userId, range.getStart(), range.getEnd()
        );

        return payments.stream()
                .sorted(Comparator.comparing(PaymentHistory::getCreatedAt).reversed()) // 최신순 정렬
                .map(p -> SummaryPaymentDto.builder()
                        .paymentCategory(p.getPaymentCategory())
                        .paymentMerchant(p.getPaymentMerchant())
                        .storeName(p.getStoreName())
                        .totalAmount(p.getTotalAmount())
                        .build())
                .collect(Collectors.toList());
    }
}
