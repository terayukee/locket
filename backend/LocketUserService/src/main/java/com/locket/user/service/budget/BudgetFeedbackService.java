package com.locket.user.service.budget;

import com.locket.payment.dto.PaymentHistoryDto;
import com.locket.user.domain.auth.entity.User;
import com.locket.user.domain.auth.repository.UserRepository;
import com.locket.user.domain.budget.dto.BudgetFeedbackRequest;
import com.locket.user.domain.budget.dto.BudgetFeedbackResponse;
import com.locket.user.domain.budget.dto.BudgetStatusResponseDto;
import com.locket.user.domain.budget.dto.ReceiptFeedbackResponse;
import com.locket.user.feign.ReceiptFeignClient;
import com.locket.user.service.payment.PaymentQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetFeedbackService {
    private final UserRepository userRepository;
    private final BudgetService budgetService;
    private final PaymentQueryService paymentQueryService;
    private final ReceiptFeignClient receiptFeignClient;

    private Map<String, Integer> calculateCategoryAmounts(List<PaymentHistoryDto> histories) {
        Map<String, Integer> totalCategoryAmount = new HashMap<>();
        for (PaymentHistoryDto payment : histories) {
            String category = payment.getPaymentCategory();
            // null 카테고리를 "기타"로 대체
            category = (category == null || category.trim().isEmpty()) ? "기타" : category;
            int amount = payment.getTotalAmount().intValue();
            totalCategoryAmount.merge(category, amount, Integer::sum);
        }
        return totalCategoryAmount;
    }

    @Cacheable(value = "budgetFeedback", key = "#userId")
    public BudgetFeedbackResponse getFeedback(Long userId) {
        log.info("사용자 {} 피드백 생성 시작", userId);

        try {
            // 1. 사용자 정보 조회
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

            // 2. 현재 월의 예산 상태 조회
            LocalDateTime now = LocalDateTime.now();
            BudgetStatusResponseDto budgetStatus = budgetService.getBudgetMonthlyStatus(
                    userId, now.getYear(), now.getMonthValue());

            if (budgetStatus == null || budgetStatus.getBudget() == null ||
                    budgetStatus.getBudget().getMonthly() == null) {
                throw new IllegalStateException("예산 정보를 찾을 수 없습니다.");
            }

            // 3. 결제 내역의 카테고리별 금액 조회
            List<PaymentHistoryDto> histories = paymentQueryService.getUserPaymentHistory(
                    userId, now.getYear(), now.getMonthValue());

            if (histories.isEmpty()) {
                log.info("사용자 {}의 결제 내역이 없습니다.", userId);
                return BudgetFeedbackResponse.builder()
                        .nickname(user.getNickname())
                        .feedback("이번 달은 소비를 아직 안하셨네요!")
                        .build();
            }

            // 카테고리별 금액 계산 (null 처리가 포함된 메서드 사용)
            Map<String, Integer> totalCategoryAmount = calculateCategoryAmounts(histories);

            log.debug("사용자 {} 이번 달 전체 카테고리별 지출: {}", userId, totalCategoryAmount);

            // 4. 피드백 요청 DTO 생성
            BudgetFeedbackRequest request = BudgetFeedbackRequest.builder()
                    .totalCategoryAmount(totalCategoryAmount)
                    .budgetStatus(budgetStatus.getBudget().getMonthly())
                    .userJob(user.getUserJob().toString())
                    .build();

            // 요청 데이터 로깅 추가
            log.info("Receipt 서비스로 전송하는 데이터: totalCategoryAmount={}, budgetStatus={}, userJob={}",
                    request.getTotalCategoryAmount(),
                    request.getBudgetStatus(),
                    request.getUserJob());

            // 5. 피드백 생성 요청
            ReceiptFeedbackResponse feedbackResponse = receiptFeignClient.generateFeedback(request);
            log.info("사용자 {} 피드백 생성 완료", userId);

            return BudgetFeedbackResponse.builder()
                    .nickname(user.getNickname())
                    .feedback(feedbackResponse.getFeedback())
                    .build();

        } catch (Exception e) {
            log.error("피드백 생성 중 오류 발생. userId: {}", userId, e);
            throw new RuntimeException("피드백 생성 중 오류가 발생했습니다.", e);
        }
    }
}