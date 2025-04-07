package com.locket.user.service.budget;

import com.locket.payment.dto.PaymentHistoryDto;
import com.locket.user.domain.budget.dto.BudgetMonthlyStatusDto;
import com.locket.user.domain.budget.dto.BudgetSetRequestDto;
import com.locket.user.domain.budget.dto.BudgetSetResponseDto;
import com.locket.user.domain.budget.dto.BudgetStatusResponseDto;
import com.locket.user.domain.budget.entity.Goals;
import com.locket.user.domain.budget.repository.GoalsRepository;
import com.locket.user.domain.budget.repository.PaymentTransactionRepository;
import com.locket.user.feign.PaymentHistoryFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetService {

    private final GoalsRepository goalsRepository;
    private final PaymentHistoryFeignClient paymentHistoryFeignClient;

    @Transactional
    public BudgetSetResponseDto setMonthlyBudget(BudgetSetRequestDto requestDto) {

        final int MAX_BUDGET_AMOUNT = 1_000_000_000;

        if (requestDto.getAmount() >= MAX_BUDGET_AMOUNT) {
            throw new IllegalArgumentException("예산 목표 설정 금액은 10억 원까지 가능합니다.");
        }

        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        // userId로 가장 최근 goals 레코드 조회
        Optional<Goals> latestGoal = goalsRepository.findTopByUserIdAndYearMonth(requestDto.getUserId(), currentYear, currentMonth);
        Goals goalEntity;

        if( latestGoal.isPresent() ){
            // 이번 달에 이미 목표가 있으면, 목표금액만 변경
            goalEntity = latestGoal.get();
            goalEntity.setGoalAmount(requestDto.getAmount());
//            log.info("이번 달 목표 업데이트: {}", goalEntity);

        }else {
            // 없으면 생성
            goalEntity = Goals.builder()
                    .userId(requestDto.getUserId())
                    .goalAmount(requestDto.getAmount())
                    .goalYear(currentYear)
                    .goalMonth(currentMonth)
                    .isAchieved(false)
                    .createdAt(now)
                    .build();
//            log.info("이번 달 첫 목표 생성: {}", goalEntity);
        }

        // DB 저장
        goalsRepository.save(goalEntity);
//        log.info("목표 저장 완료: {}", goalEntity);

        // ResponseDTO
        return BudgetSetResponseDto.builder()
                .userId(requestDto.getUserId())
                .amount(requestDto.getAmount())
                .build();
    }

    @Transactional(readOnly = true)
    public BudgetStatusResponseDto getBudgetMonthlyStatus(long userId, int year, int month) {

        // goals 테이블에서 목표 조회
        Optional<Goals> goalOpt = goalsRepository.findTopByUserIdAndYearMonth(userId, year, month);

        // 예산 설정 여부 확인
        boolean hasBudget = goalOpt.isPresent();

        if (hasBudget) {
            // 이번 달 목표 사용 금액
            int goalAmount = goalOpt.get().getGoalAmount();

            // 결제 거래 내역에서 사용 금액 합산
            List<PaymentHistoryDto> histories = paymentHistoryFeignClient.getPaymentHistories(userId, year, month);
            BigDecimal totalUsed = histories.stream()
                    .map(PaymentHistoryDto::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);


            // 남은 예산 계산
            BigDecimal remaining = BigDecimal.valueOf(goalAmount).subtract(totalUsed);
            if (remaining.compareTo(BigDecimal.ZERO) < 0) {
                remaining = BigDecimal.ZERO;
            }

            // 진행률 계산
            BigDecimal progress;
            if (goalAmount <= 0) {
                progress = BigDecimal.ZERO;
            } else {
                progress = totalUsed.multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(goalAmount), 2, RoundingMode.HALF_UP);
            }

            BudgetMonthlyStatusDto monthlyDto = BudgetMonthlyStatusDto.builder()
                    .year(year)
                    .month(month)
                    .target(goalAmount)
                    .spent(totalUsed)
                    .remaining(remaining)
                    .progress(progress)
                    .build();

            // 예산 정보가 있는 응답
            return BudgetStatusResponseDto.builder()
                    .userId(userId)
                    .hasBudget(true) // 예산이 설정되어 있음을 표시
                    .budget(BudgetStatusResponseDto.BudgetData.builder()
                            .monthly(monthlyDto)
                            .build())
                    .build();
        } else {
            // 예산이 없는 경우 빈 객체
            BudgetMonthlyStatusDto emptyMonthlyDto = BudgetMonthlyStatusDto.builder()
                    .year(year)
                    .month(month)
                    .target(0)
                    .spent(BigDecimal.ZERO)
                    .remaining(BigDecimal.ZERO)
                    .progress(BigDecimal.ZERO)
                    .build();

            // hasBudget 필드를 false로 설정하여 응답
            return BudgetStatusResponseDto.builder()
                    .userId(userId)
                    .hasBudget(false)
                    .budget(BudgetStatusResponseDto.BudgetData.builder()
                            .monthly(emptyMonthlyDto)
                            .build())
                    .build();
        }
    }

}