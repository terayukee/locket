package com.locket.elasticsearch.service.feedback;

import com.locket.elasticsearch.domain.feedback.dto.FeedbackCardStatDto;
import com.locket.elasticsearch.domain.feedback.dto.FeedbackCategoryStatDto;
import com.locket.elasticsearch.domain.feedback.dto.FeedbackDayOfWeekDto;
import com.locket.elasticsearch.domain.payment.entity.PaymentHistory;
import com.locket.elasticsearch.domain.payment.repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
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
                .map(e -> new FeedbackCategoryStatDto(e.getKey(), e.getValue(), (e.getValue() * 100.0 / total)))
                .sorted(Comparator.comparingDouble(FeedbackCategoryStatDto::getRatio).reversed())
                .collect(Collectors.toList());
    }

    public FeedbackDayOfWeekDto getDayOfWeekStats(long userId, int year, int month) {
        List<PaymentHistory> payments = paymentHistoryRepository.findByBuyerIdAndYearAndMonth(userId, year, month);
        Map<DayOfWeek, Integer> map = new EnumMap<>(DayOfWeek.class);
        for (PaymentHistory p : payments) {
            DayOfWeek day = p.getCreatedAt().getDayOfWeek();
            map.merge(day, p.getTotalAmount().intValue(), Integer::sum);
        }
        return new FeedbackDayOfWeekDto(map);
    }

    public List<FeedbackCardStatDto> getCardUsageStats(long userId, int year, int month) {
        return paymentHistoryRepository.findByBuyerIdAndYearAndMonth(userId, year, month).stream()
                .filter(p -> p.getCardName() != null)
                .collect(Collectors.groupingBy(PaymentHistory::getCardName))
                .entrySet().stream()
                .map(e -> new FeedbackCardStatDto(
                        e.getKey(),
                        e.getValue().size(),
                        e.getValue().stream().mapToInt(p -> p.getTotalAmount().intValue()).sum()
                ))
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
        int ageStart = (birthYear / 10) * 10;
        int ageEnd = ageStart + 9;

        List<PaymentHistory> userPayments = paymentHistoryRepository.findByBuyerIdAndYearAndMonth(userId, year, month);
        Map<String, Integer> userCategorySpend = userPayments.stream()
                .filter(p -> p.getPaymentCategory() != null)
                .collect(Collectors.groupingBy(PaymentHistory::getPaymentCategory,
                        Collectors.summingInt(p -> p.getTotalAmount().intValue())));

        List<PaymentHistory> groupPayments = paymentHistoryRepository.findByBirthDateBetweenAndYearAndMonth(ageStart, ageEnd, year, month);
        Map<String, Double> groupAverageSpend = groupPayments.stream()
                .filter(p -> p.getPaymentCategory() != null)
                .collect(Collectors.groupingBy(PaymentHistory::getPaymentCategory,
                        Collectors.averagingInt(p -> p.getTotalAmount().intValue())));

        return Map.of(
                "userSpending", userCategorySpend,
                "ageGroupAverage", groupAverageSpend
        );
    }

    public Map<String, Object> getMonthlyChange(long userId, int year, int month) {
        int prevYear = month == 1 ? year - 1 : year;
        int prevMonth = month == 1 ? 12 : month - 1;

        List<PaymentHistory> current = paymentHistoryRepository.findByBuyerIdAndYearAndMonth(userId, year, month);
        List<PaymentHistory> previous = paymentHistoryRepository.findByBuyerIdAndYearAndMonth(userId, prevYear, prevMonth);

        int currentTotal = current.stream().mapToInt(p -> p.getTotalAmount().intValue()).sum();
        int previousTotal = previous.stream().mapToInt(p -> p.getTotalAmount().intValue()).sum();

        double totalChangeRate = previousTotal == 0 ? 100.0 : ((currentTotal - previousTotal) * 100.0 / previousTotal);

        Map<String, Integer> currByCategory = current.stream()
                .filter(p -> p.getPaymentCategory() != null)
                .collect(Collectors.groupingBy(PaymentHistory::getPaymentCategory, Collectors.summingInt(p -> p.getTotalAmount().intValue())));

        Map<String, Integer> prevByCategory = previous.stream()
                .filter(p -> p.getPaymentCategory() != null)
                .collect(Collectors.groupingBy(PaymentHistory::getPaymentCategory, Collectors.summingInt(p -> p.getTotalAmount().intValue())));

        Map<String, Double> categoryChange = new HashMap<>();
        for (String cat : currByCategory.keySet()) {
            int curr = currByCategory.getOrDefault(cat, 0);
            int prev = prevByCategory.getOrDefault(cat, 0);
            double rate = (prev == 0) ? 100.0 : ((curr - prev) * 100.0 / prev);
            categoryChange.put(cat, rate);
        }

        return Map.of(
                "currentMonthTotal", currentTotal,
                "previousMonthTotal", previousTotal,
                "totalChangeRate", totalChangeRate,
                "categoryChangeRate", categoryChange
        );
    }

    public List<String> getHotCategories(long userId, int year, int month) {
        Map<String, Integer> month1 = getCategorySums(userId, year, month);
        int[] prev1 = getPreviousMonth(year, month);
        Map<String, Integer> month2 = getCategorySums(userId, prev1[0], prev1[1]);
        int[] prev2 = getPreviousMonth(prev1[0], prev1[1]);
        Map<String, Integer> month3 = getCategorySums(userId, prev2[0], prev2[1]);

        Set<String> allCats = new HashSet<>(month1.keySet());
        allCats.retainAll(month2.keySet());
        allCats.retainAll(month3.keySet());

        return allCats.stream()
                .filter(cat -> month1.get(cat) > month2.get(cat) && month2.get(cat) > month3.get(cat))
                .collect(Collectors.toList());
    }

    private Map<String, Integer> getCategorySums(long userId, int year, int month) {
        return paymentHistoryRepository.findByBuyerIdAndYearAndMonth(userId, year, month).stream()
                .filter(p -> p.getPaymentCategory() != null)
                .collect(Collectors.groupingBy(PaymentHistory::getPaymentCategory,
                        Collectors.summingInt(p -> p.getTotalAmount().intValue())));
    }

    private int[] getPreviousMonth(int year, int month) {
        return (month == 1) ? new int[]{year - 1, 12} : new int[]{year, month - 1};
    }

    public Double getSpendingDiversityEntropy(long userId, int year, int month) {
        List<PaymentHistory> payments = paymentHistoryRepository.findByBuyerIdAndYearAndMonth(userId, year, month);
        Map<String, Integer> byCategory = payments.stream()
                .filter(p -> p.getPaymentCategory() != null)
                .collect(Collectors.groupingBy(PaymentHistory::getPaymentCategory, Collectors.summingInt(p -> p.getTotalAmount().intValue())));

        int total = byCategory.values().stream().mapToInt(i -> i).sum();
        if (total == 0) return 0.0;

        double entropy = 0.0;
        for (int amt : byCategory.values()) {
            double p = amt / (double) total;
            entropy += -p * Math.log(p);
        }
        return entropy;
    }
}
