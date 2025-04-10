package com.locket.user.service.budget;

import com.locket.payment.dto.PaymentHistoryDto;
import com.locket.user.domain.auth.entity.User;
import com.locket.user.domain.auth.repository.UserRepository;
import com.locket.user.domain.budget.dto.BudgetFeedbackResponse;
import com.locket.user.domain.budget.dto.BudgetStatusResponseDto;
import com.locket.user.domain.budget.dto.BudgetMonthlyStatusDto;
import com.locket.user.service.payment.PaymentQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetFeedbackService {
    private final UserRepository userRepository;
    private final BudgetService budgetService;
    private final PaymentQueryService paymentQueryService;

    // 고정 메시지 맵
    private static final Map<String, String> CATEGORY_MESSAGES = Map.of(
            "카페/디저트", "가끔은 믹스커피도 맛있답니다",
            "생활", "절약의 시작은 일상에서부터!",
            "식비", "냉장고 속 음식을 활용하세요!",
            "쇼핑", "오늘은 충동구매 금지데이!",
            "교통", "오늘은 대중교통을 이용하는 하루!",
            "기타", "이번 달 소비 습관을 점검 해보세요!"
    );

    // 고정 피드백 메시지
    private static final String ZERO_BUDGET_MESSAGE = "목표 예산을 설정해보세요!";
    private static final String OVER_BUDGET_MESSAGE = "예산 초과! 정말 아껴야 해요";
    private static final String NEAR_BUDGET_MESSAGE = "예산이 얼마 안남았어요!";
    private static final String NO_SPENDING_MESSAGE = "이번 달은 아직 소비를 안하셨네요!";

    private Map<String, Integer> calculateCategoryAmounts(List<PaymentHistoryDto> histories) {
        Map<String, Integer> totalCategoryAmount = new HashMap<>();
        for (PaymentHistoryDto payment : histories) {
            String category = payment.getPaymentCategory();
            category = (category == null || category.trim().isEmpty()) ? "기타" : category;
            int amount = payment.getTotalAmount().intValue();
            totalCategoryAmount.merge(category, amount, Integer::sum);
        }
        return totalCategoryAmount;
    }

    public BudgetFeedbackResponse getFeedback(Long userId) {
        log.info("사용자 {} budgetFeedback 생성 시작", userId);

        try {
            // 1. 사용자 조회
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

            // 2. 예산 상태 조회
            LocalDateTime now = LocalDateTime.now();
            BudgetStatusResponseDto budgetStatus = budgetService.getBudgetMonthlyStatus(
                    userId, now.getYear(), now.getMonthValue());

            // 예산이 없을 경우
            if (!budgetStatus.isHasBudget() ||
                    budgetStatus.getBudget() == null ||
                    budgetStatus.getBudget().getMonthly() == null ||
                    budgetStatus.getBudget().getMonthly().getTarget() == 0) {
                return BudgetFeedbackResponse.builder()
                        .nickname(user.getNickname())
                        .feedback(ZERO_BUDGET_MESSAGE)
                        .build();
            }

            // 3. 결제 내역 조회
            List<PaymentHistoryDto> histories = paymentQueryService.getUserPaymentHistory(
                    userId, now.getYear(), now.getMonthValue());

            if (histories.isEmpty()) {
                return BudgetFeedbackResponse.builder()
                        .nickname(user.getNickname())
                        .feedback(NO_SPENDING_MESSAGE)
                        .build();
            }

            // 카테고리별 금액 계산
            Map<String, Integer> categoryAmounts = calculateCategoryAmounts(histories);

            // 예산 대비 지출 비율 계산
            BudgetMonthlyStatusDto monthlyStatus = budgetStatus.getBudget().getMonthly();
            double spentRatio = monthlyStatus.getSpent().doubleValue() / monthlyStatus.getTarget();

            String feedback;

            if (spentRatio > 1.0) {
                feedback = OVER_BUDGET_MESSAGE;
            } else if (spentRatio >= 0.9) {
                feedback = NEAR_BUDGET_MESSAGE;
            } else {
                // 가장 지출 많은 카테고리 기준으로 메시지 선택
                String topCategory = categoryAmounts.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse("기타");

                feedback = CATEGORY_MESSAGES.getOrDefault(topCategory, CATEGORY_MESSAGES.get("기타"));
            }

            log.info("사용자 {} 피드백 생성 완료", userId);
            return BudgetFeedbackResponse.builder()
                    .nickname(user.getNickname())
                    .feedback(feedback)
                    .build();

        } catch (Exception e) {
            log.error("피드백 생성 중 오류 발생. userId: {}", userId, e);
            throw new RuntimeException("피드백 생성 중 오류가 발생했습니다.", e);
        }
    }
}
