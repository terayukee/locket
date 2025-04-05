package com.locket.user.feign;

import com.locket.elastic.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

//@FeignClient(name = "elasticsearch-service", contextId = "feedbackStatClient")
@FeignClient(
        name = "elasticsearch-service",
        contextId = "feedbackStatClient",
        url = "http://172.26.5.222:8083" // 실제 컨테이너 IP 및 포트
)
public interface FeedbackStatFeignClient {

    @GetMapping("/feedback/category-stat")
    List<FeedbackCategoryStatDto> getCategoryStats(
            @RequestParam("userId") long userId,
            @RequestParam("year") int year,
            @RequestParam("month") int month
    );

    @GetMapping("/feedback/day-of-week")
    FeedbackDayOfWeekDto getDayOfWeekStats(
            @RequestParam("userId") long userId,
            @RequestParam("year") int year,
            @RequestParam("month") int month
    );

    @GetMapping("/feedback/card-stat")
    List<FeedbackCardStatDto> getCardStats(
            @RequestParam("userId") long userId,
            @RequestParam("year") int year,
            @RequestParam("month") int month
    );

    @GetMapping("/feedback/top-store")
    TopStoreStatDto getTopSpendingStore(
            @RequestParam("userId") long userId,
            @RequestParam("year") int year,
            @RequestParam("month") int month
    );

    @GetMapping("/feedback/category-compare-age")
    FeedbackAgeGroupComparisonDto getAgeComparison(
            @RequestParam("userId") long userId,
            @RequestParam("birthYear") int birthYear,
            @RequestParam("year") int year,
            @RequestParam("month") int month
    );

    @GetMapping("/feedback/compare-previous-month")
    FeedbackMonthlyChangeDto getPreviousMonthComparison(
            @RequestParam("userId") long userId,
            @RequestParam("year") int year,
            @RequestParam("month") int month
    );

    @GetMapping("/feedback/hot-categories")
    HotCategoriesDto getHotCategories(
            @RequestParam("userId") long userId,
            @RequestParam("year") int year,
            @RequestParam("month") int month
    );

    @GetMapping("/feedback/spending-entropy")
    SpendingEntropyDto getSpendingEntropy(
            @RequestParam("userId") long userId,
            @RequestParam("year") int year,
            @RequestParam("month") int month
    );
}
