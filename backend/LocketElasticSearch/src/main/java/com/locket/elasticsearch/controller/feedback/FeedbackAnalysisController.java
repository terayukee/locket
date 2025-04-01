package com.locket.elasticsearch.controller.feedback;


import com.locket.elastic.dto.*;
import com.locket.elasticsearch.service.feedback.FeedbackStatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "소비 분석 서비스")
@Slf4j
@RestController
@RequestMapping("/api/elasticsearch/feedback")
@RequiredArgsConstructor
public class FeedbackAnalysisController {

    private final FeedbackStatService feedbackStatService;

    @GetMapping("/category-stat")
    @Operation(summary = "카테고리별 과소비 통계", description = "전체 소비 중 카테고리별 비율을 반환합니다.")
    public ResponseEntity<List<FeedbackCategoryStatDto>> getCategoryStats(
            @RequestParam long userId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return ResponseEntity.ok(feedbackStatService.getCategoryStats(userId, year, month));
    }

    @GetMapping("/day-of-week")
    @Operation(summary = "요일별 지출 통계", description = "요일별 지출 금액 합계를 반환합니다.")
    public ResponseEntity<FeedbackDayOfWeekDto> getDayOfWeekStats(
            @RequestParam long userId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return ResponseEntity.ok(feedbackStatService.getDayOfWeekStats(userId, year, month));
    }

    @GetMapping("/card-stat")
    @Operation(summary = "카드별 사용 비중", description = "카드 이름 기준 사용 횟수 및 총 지출 통계를 반환합니다.")
    public ResponseEntity<List<FeedbackCardStatDto>> getCardStat(
            @RequestParam long userId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return ResponseEntity.ok(feedbackStatService.getCardUsageStats(userId, year, month));
    }

    @GetMapping("/top-store")
    @Operation(summary = "가장 많이 소비한 가게", description = "해당 월 사용자 기준 가장 많이 소비한 storeName을 반환합니다.")
    public ResponseEntity<TopStoreStatDto> getTopSpendingStore(
            @RequestParam long userId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return ResponseEntity.ok(feedbackStatService.getTopSpendingStore(userId, year, month));
    }


    @GetMapping("/category-compare-age")
    @Operation(summary = "카테고리별 연령대 평균 비교", description = "사용자의 연령대와 같은 그룹과 카테고리별 소비 평균을 비교합니다.")
    public ResponseEntity<FeedbackAgeGroupComparisonDto> getCategoryComparisonWithAgeGroup(
            @RequestParam long userId,
            @RequestParam int birthYear,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(feedbackStatService.compareWithAgeGroup(userId, birthYear, year, month));
    }

    @GetMapping("/compare-previous-month")
    @Operation(summary = "전월 대비 지출 증감률 분석", description = "사용자의 전월 대비 전체 및 카테고리별 소비 증감률을 반환합니다.")
    public ResponseEntity<FeedbackMonthlyChangeDto> getMonthlyChange(
            @RequestParam long userId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(feedbackStatService.getMonthlyChange(userId, year, month));
    }

    @GetMapping("/hot-categories")
    @Operation(summary = "지속적으로 증가한 소비 카테고리", description = "최근 3개월간 소비가 지속적으로 증가한 카테고리를 반환합니다.")
    public ResponseEntity<HotCategoriesDto> getHotCategories(
            @RequestParam long userId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(feedbackStatService.getHotCategories(userId, year, month));
    }

    @GetMapping("/spending-entropy")
    @Operation(summary = "지출 다양성 지수", description = "카테고리별 소비 분산도를 바탕으로 Shannon entropy 값을 반환합니다.")
    public ResponseEntity<SpendingEntropyDto> getSpendingEntropy(
            @RequestParam long userId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(feedbackStatService.getSpendingDiversityEntropy(userId, year, month));
    }
}
