package com.locket.user.controller.budget;

import com.locket.user.domain.budget.dto.BudgetSetRequestDto;
import com.locket.user.domain.budget.dto.BudgetSetResponseDto;
import com.locket.user.domain.budget.dto.BudgetStatusResponseDto;
import com.locket.user.exception.ErrorResponse;
import com.locket.user.service.budget.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/budget")
@Tag(name = "\uD83D\uDCB0Budget", description = "소비 목표")
public class BudgetController {

    private final BudgetService budgetService;

    @Operation(summary = "예산 목표 설정", description = "이미 존재하는 목표가 있으면 갱신, 없으면 신규 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS"),
            @ApiResponse(
                    responseCode = "401",
                    description = "UNAUTHORIZED",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 401,
                                              "error": "Unauthorized",
                                              "message": "인증되지 않은 사용자입니다.",
                                              "timestamp": "2025-04-01T12:34:56.789Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리소스를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "리소스를 찾을 수 없습니다.",
                                              "timestamp": "2025-04-01T12:34:56.789Z"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/set")
    public BudgetSetResponseDto setMonthlyBudget(@Valid @RequestBody BudgetSetRequestDto requestDto) {
        return budgetService.setMonthlyBudget(requestDto);
    }

    @Operation(summary = "예산 사용 현황 조회", description = "특정 달의 예산 사용 현황을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS"),
            @ApiResponse(
                    responseCode = "401",
                    description = "UNAUTHORIZED",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 401,
                                              "error": "Unauthorized",
                                              "message": "인증되지 않은 사용자입니다.",
                                              "timestamp": "2025-04-01T12:34:56.789Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리소스를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "리소스를 찾을 수 없습니다.",
                                              "timestamp": "2025-04-01T12:34:56.789Z"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/status")
    public BudgetStatusResponseDto getBudgetMonthlyStatus(
            @RequestParam("userId") long userId,
            @RequestParam("year") int year,
            @RequestParam("month") int month
    ) {
        return budgetService.getBudgetMonthlyStatus(userId, year, month);
    }
}
