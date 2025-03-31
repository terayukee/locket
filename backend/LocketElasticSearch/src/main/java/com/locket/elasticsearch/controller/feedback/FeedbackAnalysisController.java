package com.locket.elasticsearch.controller.feedback;

import com.locket.elasticsearch.domain.feedback.dto.FeedbackCategoryStatDto;
import com.locket.elasticsearch.domain.feedback.dto.FeedbackDayOfWeekDto;
import com.locket.elasticsearch.domain.feedback.dto.FeedbackCardStatDto;
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
}
