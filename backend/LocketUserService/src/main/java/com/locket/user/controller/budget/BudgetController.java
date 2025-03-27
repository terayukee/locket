package com.locket.user.controller.budget;

import com.locket.user.domain.budget.dto.BudgetSetRequestDto;
import com.locket.user.domain.budget.dto.BudgetSetResponseDto;
import com.locket.user.domain.budget.dto.BudgetStatusResponseDto;
import com.locket.user.service.budget.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/budget")
@Tag(name = "Budget", description = "소비 목표")
public class BudgetController {

    private final BudgetService budgetService;

    @Operation(summary = "예산 목표 설정", description = "이미 존재하는 목표가 있으면 갱신, 없으면 신규 생성합니다")
    @PostMapping("/set")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS"),
            @ApiResponse(responseCode = "404", description = "ACCESS_DENIED"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED")
    })
    public BudgetSetResponseDto setMonthlyBudget(@Valid @RequestBody BudgetSetRequestDto requestDto) {
        return budgetService.setMonthlyBudget(requestDto);
    }


    @Operation(summary = "예산 사용 현황 조회", description = "특정 달의 예산 사용 현황을 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS"),
            @ApiResponse(responseCode = "404", description = "ACCESS_DENIED"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED")
    })
    @GetMapping("/status/{userId}")
    public BudgetStatusResponseDto getBudgetMonthlyStatus(
            @PathVariable("userId") long userId,
            @RequestParam("year") int year,
            @RequestParam("month") int month
    ) {
        return budgetService.getBudgetMonthlyStatus(userId, year, month);
    }



}
