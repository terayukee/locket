package com.locket.user.controller.feedback;

import com.locket.user.service.feedback.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "📊 소비 피드백", description = "소비 패턴 분석 결과를 조회합니다.")
@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Operation(
            summary = "🔍 소비 피드백 조회",
            description = """
                해당 사용자의 연/월 소비 패턴 피드백을 조회합니다.  
                
                | 조건 | 처리 방법 |
                |------|------------|
                | 해당 연/월 피드백 없음 | ➡ 분석 → 저장 → 조회 |
                | 해당 연/월 피드백 존재 + 이번 달 | ➡ 재분석 → 업데이트 → 조회 |
                | 해당 연/월 피드백 존재 + 과거 달 | ➡ 기존 피드백만 조회 |
                """,
            parameters = {
                    @Parameter(name = "userId", description = "사용자 ID", example = "1", required = true),
                    @Parameter(name = "year", description = "년도 (YYYY)", example = "2025", required = true),
                    @Parameter(name = "month", description = "월 (MM)", example = "3", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "피드백 조회 성공", content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "피드백 응답 예시",
                                    value = """
                                            {
                                              "totalAmount": 250000,
                                              "categoryBreakdown": [
                                                {
                                                  "category": "식비",
                                                  "amount": 150000,
                                                  "percentage": 60
                                                },
                                                {
                                                  "category": "교통",
                                                  "amount": 100000,
                                                  "percentage": 40
                                                }
                                              ],
                                              "dominantCategory": "식비",
                                              "characterImageUrl": "https://example.com/character.png",
                                              "characterName": "카페인 뱀파이어",
                                              "summary": "이번 달 식비 비중이 높습니다. 간편식 줄이기를 시도해보세요.",
                                              "patternAnalysis": [
                                                "식비에 집중된 지출",
                                                "소액 결제가 많음",
                                                "영수증 업로드율 낮음"
                                              ],
                                              "improvement": [
                                                "지출 알림 설정",
                                                "식비 상한 설정",
                                                "예산 초과시 경고 메시지 표시"
                                              ]
                                            }
                                            """
                            )
                    )),
                    @ApiResponse(responseCode = "401", description = "JWT 인증 실패 (UNAUTHORIZED)"),
                    @ApiResponse(responseCode = "404", description = "해당 사용자의 피드백 없음 (ACCESS_DENIED)")
            }
    )
    @GetMapping
    public ResponseEntity<?> getFeedback(
            @RequestParam Long userId,
            @RequestParam Integer year,
            @RequestParam Integer month
    ) {
        return feedbackService.handleFeedbackRequest(userId, year, month);
    }
}
