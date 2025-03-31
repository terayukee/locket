package com.locket.elasticsearch.service.feedback;

import com.locket.elasticsearch.domain.feedback.dto.FeedbackCategoryStatDto;
import com.locket.elasticsearch.domain.feedback.dto.FeedbackCardStatDto;
import com.locket.elasticsearch.domain.feedback.dto.FeedbackDayOfWeekDto;
import com.locket.elasticsearch.domain.payment.entity.PaymentHistory;
import com.locket.elasticsearch.domain.payment.repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackStatService {

    private final PaymentHistoryRepository paymentHistoryRepository;

    public List<FeedbackCategoryStatDto> getCategoryStats(long userId, int year, int month) {
        List<PaymentHistory> payments = paymentHistoryRepository.findByBuyerIdAndYearAndMonth(userId, year, month);

        int total = payments.stream().mapToInt(p -> p.getTotalAmount().intValue()).sum();

        return payments.stream()
                .filter(p -> p.getPaymentCategory() != null)
                .collect(Collectors.groupingBy(PaymentHistory::getPaymentCategory, Collectors.summingInt(p -> p.getTotalAmount().intValue())))
                .entrySet().stream()
                .map(entry -> new FeedbackCategoryStatDto(entry.getKey(), entry.getValue(), total == 0 ? 0 : (entry.getValue() * 100.0 / total)))
                .sorted(Comparator.comparingDouble(FeedbackCategoryStatDto::getRatio).reversed())
                .collect(Collectors.toList());
    }

    public FeedbackDayOfWeekDto getDayOfWeekStats(long userId, int year, int month) {
        List<PaymentHistory> payments = paymentHistoryRepository.findByBuyerIdAndYearAndMonth(userId, year, month);

        Map<DayOfWeek, Integer> spendingByDay = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek day : DayOfWeek.values()) spendingByDay.put(day, 0);

        for (PaymentHistory p : payments) {
            DayOfWeek day = p.getCreatedAt().getDayOfWeek();
            spendingByDay.put(day, spendingByDay.get(day) + p.getTotalAmount().intValue());
        }

        return new FeedbackDayOfWeekDto(spendingByDay);
    }

    public List<FeedbackCardStatDto> getCardUsageStats(long userId, int year, int month) {
        List<PaymentHistory> payments = paymentHistoryRepository.findByBuyerIdAndYearAndMonth(userId, year, month);

        Map<String, List<PaymentHistory>> cardMap = payments.stream()
                .filter(p -> p.getCardName() != null)
                .collect(Collectors.groupingBy(PaymentHistory::getCardName));

        return cardMap.entrySet().stream()
                .map(entry -> {
                    int totalAmount = entry.getValue().stream().mapToInt(p -> p.getTotalAmount().intValue()).sum();
                    int count = entry.getValue().size();
                    return new FeedbackCardStatDto(entry.getKey(), count, totalAmount);
                })
                .sorted(Comparator.comparingInt(FeedbackCardStatDto::getTotalAmount).reversed())
                .collect(Collectors.toList());
    }

    public String getTopSpendingStore(long userId, int year, int month) {
        List<PaymentHistory> payments = paymentHistoryRepository.findByBuyerIdAndYearAndMonth(userId, year, month);

        return payments.stream()
                .filter(p -> p.getStoreName() != null)
                .collect(Collectors.groupingBy(PaymentHistory::getStoreName, Collectors.summingInt(p -> p.getTotalAmount().intValue())))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("가장 많이 소비한 가게가 없습니다.");
    }

    public Map<String, Object> compareWithAgeGroup(long userId, int birthYear, int year, int month) {
        // 위 아래 3살차이 까지
        int ageStart = birthYear - 3;
        int ageEnd = ageStart + 3;

        // 사용자 소비 내역
        List<PaymentHistory> userPayments = paymentHistoryRepository.findByBuyerIdAndYearAndMonth(userId, year, month);
        Map<String, Integer> userCategorySpend = userPayments.stream()
                .filter(p -> p.getPaymentCategory() != null)
                .collect(Collectors.groupingBy(PaymentHistory::getPaymentCategory,
                        Collectors.summingInt(p -> p.getTotalAmount().intValue())));

        // 연령대 사용자 소비 내역
        List<PaymentHistory> groupPayments = paymentHistoryRepository
                .findByBirthDateBetweenAndYearAndMonth(ageStart, ageEnd, year, month);
        Map<String, Double> groupAverageSpend = groupPayments.stream()
                .filter(p -> p.getPaymentCategory() != null)
                .collect(Collectors.groupingBy(PaymentHistory::getPaymentCategory,
                        Collectors.averagingInt(p -> p.getTotalAmount().intValue())));

        return Map.of(
                "userSpending", userCategorySpend,
                "ageGroupAverage", groupAverageSpend
        );
    }

}
