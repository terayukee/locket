package com.locket.elasticsearch.service.feedback;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.json.JsonData;
import com.locket.elastic.dto.*;
import com.locket.elasticsearch.domain.payment.entity.PaymentHistory;
import com.locket.elasticsearch.domain.payment.repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedbackStatService {

    private final PaymentHistoryRepository paymentHistoryRepository;
    private final ElasticsearchOperations elasticsearchOperations;

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

        Map<DayOfWeek, Integer> dayOfWeekMap = new HashMap<>();
        for (PaymentHistory payment : payments) {
            if (payment.getCreatedAt() != null) {
                DayOfWeek day = payment.getCreatedAt().getDayOfWeek();
                int amount = payment.getTotalAmount().intValue();
                dayOfWeekMap.merge(day, amount, Integer::sum);
            }
        }

        Map<String, Integer> stringKeyMap = dayOfWeekMap.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        Map.Entry::getValue
                ));

        return new FeedbackDayOfWeekDto(stringKeyMap);
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

    public TopStoreStatDto getTopSpendingStore(long userId, int year, int month) {
        List<PaymentHistory> payments = paymentHistoryRepository.findByBuyerIdAndYearAndMonth(userId, year, month);

        return payments.stream()
                .filter(p -> p.getStoreName() != null)
                .collect(Collectors.groupingBy(PaymentHistory::getStoreName,
                        Collectors.summingInt(p -> p.getTotalAmount().intValue())))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> new TopStoreStatDto(e.getKey(), e.getValue()))
                .orElse(new TopStoreStatDto("가장 많이 소비한 가게가 없습니다.", 0));
    }

    public FeedbackAgeGroupComparisonDto compareWithAgeGroup(long userId, int birthYear, int year, int month) {
        // 위 아래 3살 차이까지
        int ageStart = birthYear - 3;
        int ageEnd = birthYear + 3;

        List<PaymentHistory> userPayments = paymentHistoryRepository.findByBuyerIdAndYearAndMonth(userId, year, month);
        Map<String, Integer> userCategorySpend = userPayments.stream()
                .filter(p -> p.getPaymentCategory() != null)
                .collect(Collectors.groupingBy(PaymentHistory::getPaymentCategory,
                        Collectors.summingInt(p -> p.getTotalAmount().intValue())));

        List<PaymentHistory> groupPayments = findByAgeGroupAndMonth(ageStart, ageEnd, year, month);
        Map<String, Double> groupAverageSpend = groupPayments.stream()
                .filter(p -> p.getPaymentCategory() != null)
                .collect(Collectors.groupingBy(PaymentHistory::getPaymentCategory,
                        Collectors.averagingInt(p -> p.getTotalAmount().intValue())));

        return new FeedbackAgeGroupComparisonDto(userCategorySpend, groupAverageSpend);
    }

    // ✅ 연령대 사용자들의 해당 월 결제 내역 조회
    public List<PaymentHistory> findByAgeGroupAndMonth(int birthStart, int birthEnd, int year, int month) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(Query.of(q -> q
                        .bool(b -> b
                                .must(
                                        Query.of(q1 -> q1.range(r -> r.field("birthDate").gte(JsonData.of(birthStart)).lte(JsonData.of(birthEnd)))),
                                        Query.of(q2 -> q2.term(t -> t.field("year").value(year))),
                                        Query.of(q3 -> q3.term(t -> t.field("month").value(month)))
                                )
                        )
                ))
                .build();

        return elasticsearchOperations
                .search(query, PaymentHistory.class)
                .stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    public FeedbackMonthlyChangeDto getMonthlyChange(long userId, int year, int month) {
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

        return new FeedbackMonthlyChangeDto(currentTotal, previousTotal, totalChangeRate, categoryChange);
    }

    public HotCategoriesDto getHotCategories(long userId, int year, int month) {
        Map<String, Integer> month1 = getCategorySums(userId, year, month);
        int[] prev1 = getPreviousMonth(year, month);
        Map<String, Integer> month2 = getCategorySums(userId, prev1[0], prev1[1]);
        int[] prev2 = getPreviousMonth(prev1[0], prev1[1]);
        Map<String, Integer> month3 = getCategorySums(userId, prev2[0], prev2[1]);

        Set<String> allCats = new HashSet<>(month1.keySet());
        allCats.retainAll(month2.keySet());
        allCats.retainAll(month3.keySet());

        List<String> increasingCats = allCats.stream()
                .filter(cat -> month1.get(cat) > month2.get(cat) && month2.get(cat) > month3.get(cat))
                .collect(Collectors.toList());

        return new HotCategoriesDto(increasingCats);
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

    public SpendingEntropyDto getSpendingDiversityEntropy(long userId, int year, int month) {
        List<PaymentHistory> payments = paymentHistoryRepository.findByBuyerIdAndYearAndMonth(userId, year, month);
        Map<String, Integer> byCategory = payments.stream()
                .filter(p -> p.getPaymentCategory() != null)
                .collect(Collectors.groupingBy(PaymentHistory::getPaymentCategory, Collectors.summingInt(p -> p.getTotalAmount().intValue())));

        int total = byCategory.values().stream().mapToInt(i -> i).sum();
        if (total == 0) return new SpendingEntropyDto(0.0);

        double entropy = 0.0;
        for (int amt : byCategory.values()) {
            double p = amt / (double) total;
            entropy += -p * Math.log(p);
        }
        return new SpendingEntropyDto(entropy);
    }
}
