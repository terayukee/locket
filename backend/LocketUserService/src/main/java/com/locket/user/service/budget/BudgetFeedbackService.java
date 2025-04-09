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

    private static final Map<String, List<String>> CATEGORY_MESSAGES = Map.of(
            "카페/디저트", Arrays.asList(
                    "오늘 커피는 홈카페 어떠세요?",
                    "가끔은 믹스커피도 맛있답니다",
                    "카페보다는 자판기로 여유를 즐겨봐요"
            ),
            "생활", Arrays.asList(
                    "절약의 시작은 일상에서부터!",
                    "무료한 일상도 가끔은 좋답니다"
            ),
            "식비", Arrays.asList(
                    "오늘 저녁은 집에서 먹기!",
                    "냉장고 속 음식을 활용하세요!",
                    "자취 요리로 하루를 마무리해봐요"
            ),
            "쇼핑", Arrays.asList(
                    "오늘은 충동구매 금지데이!",
                    "장바구니에 담기 전에 고민부터!",
                    "사는 즐거움 대신 고르는 여유를!"
            ),
            "교통", Arrays.asList(
                    "가까운 거리는 걸어보세요!",
                    "오늘은 대중교통을 이용하는 하루!"
            ),
            "기타", Arrays.asList(
                    "소비 습관을 돌아보는 하루!",
                    "이번 달 소비 습관 점검 해보세요!"
            )
    );

    private static final List<String> ZERO_BUDGET_MESSAGES = Arrays.asList(
            "목표 예산을 설정해보세요!",
            "아직 예산을 설정하지 않으셨어요!",
            "목표 예산부터 정해보세요!"
    );

    private static final List<String> OVER_BUDGET_MESSAGES = Arrays.asList(
            "목표 예산 초과! 소비를 줄이세요",
            "예산 초과! 정말 아껴야 해요!",
            "이번 달 지출, 매우 위태로워요!"
    );

    private static final List<String> NEAR_BUDGET_MESSAGES = Arrays.asList(
            "목표 예산 도달이 가까워졌어요!",
            "예산이 얼마 안남았어요!"
    );

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

    private String getRandomMessage(List<String> messages) {
        return messages.get(new Random().nextInt(messages.size()));
    }

    public BudgetFeedbackResponse getFeedback(Long userId) {
        log.info("사용자 {} budgetFeedback 생성 시작", userId);

        try {
            // 1. 사용자 정보 조회
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

            // 2. 현재 월의 예산 상태 조회
            LocalDateTime now = LocalDateTime.now();
            BudgetStatusResponseDto budgetStatus = budgetService.getBudgetMonthlyStatus(
                    userId, now.getYear(), now.getMonthValue());

            // 목표 예산이 없는 경우
            if (!budgetStatus.isHasBudget() || budgetStatus.getBudget() == null ||
                    budgetStatus.getBudget().getMonthly() == null ||
                    budgetStatus.getBudget().getMonthly().getTarget() == 0) {
                return BudgetFeedbackResponse.builder()
                        .nickname(user.getNickname())
                        .feedback(getRandomMessage(ZERO_BUDGET_MESSAGES))
                        .build();
            }

            // 3. 결제 내역의 카테고리별 금액 조회
            List<PaymentHistoryDto> histories = paymentQueryService.getUserPaymentHistory(
                    userId, now.getYear(), now.getMonthValue());

            if (histories.isEmpty()) {
                return BudgetFeedbackResponse.builder()
                        .nickname(user.getNickname())
                        .feedback("이번 달은 소비를 아직 안하셨네요!")
                        .build();
            }

            // 카테고리별 금액 계산
            Map<String, Integer> categoryAmounts = calculateCategoryAmounts(histories);

            // 예산 대비 지출 비율 계산
            BudgetMonthlyStatusDto monthlyStatus = budgetStatus.getBudget().getMonthly();
            double spentRatio = monthlyStatus.getSpent().doubleValue() / monthlyStatus.getTarget();

            // 피드백 메시지 결정
            String feedback;
            if (spentRatio > 1.0) {
                // 목표 예산 초과
                feedback = getRandomMessage(OVER_BUDGET_MESSAGES);
            } else if (spentRatio >= 0.9) {
                // 목표 예산의 90% 이상
                feedback = getRandomMessage(NEAR_BUDGET_MESSAGES);
            } else {
                // 가장 지출이 많은 카테고리 찾기
                String topCategory = categoryAmounts.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse("기타");

                // 해당 카테고리의 메시지 중 랜덤 선택
                List<String> categoryMessages = CATEGORY_MESSAGES.getOrDefault(
                        topCategory,
                        CATEGORY_MESSAGES.get("기타")
                );
                feedback = getRandomMessage(categoryMessages);
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