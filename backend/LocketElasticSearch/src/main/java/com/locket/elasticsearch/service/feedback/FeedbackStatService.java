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

    // 카테고리별 소비 통계
    public List<FeedbackCategoryStatDto> getCategoryStats(long userId, int year, int month) {
        List<PaymentHistory> payments = paymentHistoryRepository.findByBuyerIdAndYearAndMonth(userId, year, month);
        double total = payments.stream().mapToDouble(p -> p.getTotalAmount().doubleValue()).sum();

        return payments.stream()
                .filter(p -> p.getPaymentCategory() != null)
                .collect(Collectors.groupingBy(PaymentHistory::getPaymentCategory, Collectors.summingDouble(p -> p.getTotalAmount().doubleValue())))
                .entrySet().stream()
                .map(e -> new FeedbackCategoryStatDto(e.getKey(), e.getValue(), (e.getValue() * 100.0 / total)))
                .sorted(Comparator.comparingDouble(FeedbackCategoryStatDto::getRatio).reversed())
                .collect(Collectors.toList());
    }

    // 요일별 소비 통계
    public FeedbackDayOfWeekDto getDayOfWeekStats(long userId, int year, int month) {
        List<PaymentHistory> payments = paymentHistoryRepository.findByBuyerIdAndYearAndMonth(userId, year, month);

        Map<DayOfWeek, Double> dayOfWeekMap = new HashMap<>();
        for (PaymentHistory payment : payments) {
            if (payment.getCreatedAt() != null) {
                DayOfWeek day = payment.getCreatedAt().getDayOfWeek();
                double amount = payment.getTotalAmount().doubleValue();
                dayOfWeekMap.merge(day, amount, Double::sum);
            }
        }

        Map<String, Double> stringKeyMap = dayOfWeekMap.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        Map.Entry::getValue
                ));

        return new FeedbackDayOfWeekDto(stringKeyMap);
    }

    // 카드별 사용 통계
    public List<FeedbackCardStatDto> getCardUsageStats(long userId, int year, int month) {
        return paymentHistoryRepository.findByBuyerIdAndYearAndMonth(userId, year, month).stream()
                .filter(p -> p.getCardName() != null)
                .collect(Collectors.groupingBy(PaymentHistory::getCardName))
                .entrySet().stream()
                .map(e -> new FeedbackCardStatDto(
                        e.getKey(),
                        e.getValue().size(),
                        e.getValue().stream().mapToDouble(p -> p.getTotalAmount().doubleValue()).sum()
                ))
                .sorted(Comparator.comparingDouble(FeedbackCardStatDto::getTotalAmount).reversed())
                .collect(Collectors.toList());
    }

    // 가장 많이 소비한 가게
    public TopStoreStatDto getTopSpendingStore(long userId, int year, int month) {
        List<PaymentHistory> payments = paymentHistoryRepository.findByBuyerIdAndYearAndMonth(userId, year, month);

        return payments.stream()
                .filter(p -> p.getStoreName() != null)
                .collect(Collectors.groupingBy(PaymentHistory::getStoreName,
                        Collectors.summingDouble(p -> p.getTotalAmount().doubleValue())))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> new TopStoreStatDto(e.getKey(), e.getValue()))
                .orElse(new TopStoreStatDto("가장 많이 소비한 가게가 없습니다.", 0.0));
    }

    // 연령대 평균과 비교
    public FeedbackAgeGroupComparisonDto compareWithAgeGroup(long userId, int birthYear, int year, int month) {
        int ageStart = birthYear - 3;
        int ageEnd = birthYear + 3;

        List<PaymentHistory> userPayments = paymentHistoryRepository.findByBuyerIdAndYearAndMonth(userId, year, month);
        Map<String, Double> userCategorySpend = userPayments.stream()
                .filter(p -> p.getPaymentCategory() != null)
                .collect(Collectors.groupingBy(PaymentHistory::getPaymentCategory,
                        Collectors.summingDouble(p -> p.getTotalAmount().doubleValue())));

        List<PaymentHistory> groupPayments = findByAgeGroupAndMonth(ageStart, ageEnd, year, month);
        Map<String, Double> groupAverageSpend = groupPayments.stream()
                .filter(p -> p.getPaymentCategory() != null)
                .collect(Collectors.groupingBy(PaymentHistory::getPaymentCategory,
                        Collectors.averagingDouble(p -> p.getTotalAmount().doubleValue())));

        return new FeedbackAgeGroupComparisonDto(userCategorySpend, groupAverageSpend);
    }

    // 연령대 및 월에 해당하는 결제 내역 조회
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

    // 전월 대비 비교 통계
    public FeedbackMonthlyChangeDto getMonthlyChange(long userId, int year, int month) {
        int prevYear = month == 1 ? year - 1 : year;
        int prevMonth = month == 1 ? 12 : month - 1;

        List<PaymentHistory> current = paymentHistoryRepository.findByBuyerIdAndYearAndMonth(userId, year, month);
        List<PaymentHistory> previous = paymentHistoryRepository.findByBuyerIdAndYearAndMonth(userId, prevYear, prevMonth);

        double currentTotal = current.stream().mapToDouble(p -> p.getTotalAmount().doubleValue()).sum();
        double previousTotal = previous.stream().mapToDouble(p -> p.getTotalAmount().doubleValue()).sum();

        double totalChangeRate = previousTotal == 0 ? 100.0 : ((currentTotal - previousTotal) * 100.0 / previousTotal);

        Map<String, Double> currByCategory = current.stream()
                .filter(p -> p.getPaymentCategory() != null)
                .collect(Collectors.groupingBy(PaymentHistory::getPaymentCategory, Collectors.summingDouble(p -> p.getTotalAmount().doubleValue())));

        Map<String, Double> prevByCategory = previous.stream()
                .filter(p -> p.getPaymentCategory() != null)
                .collect(Collectors.groupingBy(PaymentHistory::getPaymentCategory, Collectors.summingDouble(p -> p.getTotalAmount().doubleValue())));

        Map<String, Double> categoryChange = new HashMap<>();
        for (String cat : currByCategory.keySet()) {
            double curr = currByCategory.getOrDefault(cat, 0.0);
            double prev = prevByCategory.getOrDefault(cat, 0.0);
            double rate = (prev == 0.0) ? 100.0 : ((curr - prev) * 100.0 / prev);
            categoryChange.put(cat, rate);
        }

        return new FeedbackMonthlyChangeDto(currentTotal, previousTotal, totalChangeRate, categoryChange);
    }

    // 최근 3개월 간 소비가 증가한 핫 카테고리 분석
    public HotCategoriesDto getHotCategories(long userId, int year, int month) {
        Map<String, Double> month1 = getCategorySums(userId, year, month);
        int[] prev1 = getPreviousMonth(year, month);
        Map<String, Double> month2 = getCategorySums(userId, prev1[0], prev1[1]);
        int[] prev2 = getPreviousMonth(prev1[0], prev1[1]);
        Map<String, Double> month3 = getCategorySums(userId, prev2[0], prev2[1]);

        Set<String> allCats = new HashSet<>(month1.keySet());
        allCats.retainAll(month2.keySet());
        allCats.retainAll(month3.keySet());

        List<String> increasingCats = allCats.stream()
                .filter(cat -> month1.get(cat) > month2.get(cat) && month2.get(cat) > month3.get(cat))
                .collect(Collectors.toList());

        return new HotCategoriesDto(increasingCats);
    }

    // 특정 월 카테고리별 합계
    private Map<String, Double> getCategorySums(long userId, int year, int month) {
        return paymentHistoryRepository.findByBuyerIdAndYearAndMonth(userId, year, month).stream()
                .filter(p -> p.getPaymentCategory() != null)
                .collect(Collectors.groupingBy(PaymentHistory::getPaymentCategory,
                        Collectors.summingDouble(p -> p.getTotalAmount().doubleValue())));
    }

    // 이전 달 계산
    private int[] getPreviousMonth(int year, int month) {
        return (month == 1) ? new int[]{year - 1, 12} : new int[]{year, month - 1};
    }

    // 소비 다양성 지수 (Shannon entropy)
    public SpendingEntropyDto getSpendingDiversityEntropy(long userId, int year, int month) {
        List<PaymentHistory> payments = paymentHistoryRepository.findByBuyerIdAndYearAndMonth(userId, year, month);
        Map<String, Double> byCategory = payments.stream()
                .filter(p -> p.getPaymentCategory() != null)
                .collect(Collectors.groupingBy(PaymentHistory::getPaymentCategory, Collectors.summingDouble(p -> p.getTotalAmount().doubleValue())));

        double total = byCategory.values().stream().mapToDouble(i -> i).sum();
        if (total == 0) return new SpendingEntropyDto(0.0);

        double entropy = 0.0;
        for (double amt : byCategory.values()) {
            double p = amt / total;
            entropy += -p * Math.log(p);
        }
        return new SpendingEntropyDto(entropy);
    }
}
