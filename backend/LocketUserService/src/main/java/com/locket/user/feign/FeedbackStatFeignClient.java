package com.locket.user.feign;

import com.locket.elastic.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "elasticsearch-service", contextId = "feedbackStatClient")
public interface FeedbackStatFeignClient {

    @GetMapping("/api/elasticsearch/feedback/category-stat")
    List<FeedbackCategoryStatDto> getCategoryStats(
            @RequestParam("userId") long userId,
            @RequestParam("year") int year,
            @RequestParam("month") int month
    );

    @GetMapping("/api/elasticsearch/feedback/day-of-week")
    FeedbackDayOfWeekDto getDayOfWeekStats(
            @RequestParam("userId") long userId,
            @RequestParam("year") int year,
            @RequestParam("month") int month
    );

    @GetMapping("/api/elasticsearch/feedback/card-stat")
    List<FeedbackCardStatDto> getCardStats(
            @RequestParam("userId") long userId,
            @RequestParam("year") int year,
            @RequestParam("month") int month
    );

    @GetMapping("/api/elasticsearch/feedback/top-store")
    TopStoreStatDto getTopSpendingStore(
            @RequestParam("userId") long userId,
            @RequestParam("year") int year,
            @RequestParam("month") int month
    );

    @GetMapping("/api/elasticsearch/feedback/category-compare-age")
    FeedbackAgeGroupComparisonDto getAgeComparison(
            @RequestParam("userId") long userId,
            @RequestParam("birthYear") int birthYear,
            @RequestParam("year") int year,
            @RequestParam("month") int month
    );

    @GetMapping("/api/elasticsearch/feedback/compare-previous-month")
    FeedbackMonthlyChangeDto getPreviousMonthComparison(
            @RequestParam("userId") long userId,
            @RequestParam("year") int year,
            @RequestParam("month") int month
    );

    @GetMapping("/api/elasticsearch/feedback/hot-categories")
    HotCategoriesDto getHotCategories(
            @RequestParam("userId") long userId,
            @RequestParam("year") int year,
            @RequestParam("month") int month
    );

    @GetMapping("/api/elasticsearch/feedback/spending-entropy")
    SpendingEntropyDto getSpendingEntropy(
            @RequestParam("userId") long userId,
            @RequestParam("year") int year,
            @RequestParam("month") int month
    );
}
