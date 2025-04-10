package com.locket.user.service.budget;

import com.locket.user.domain.auth.entity.User;
import com.locket.user.domain.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetFeedbackScheduler {
    private final BudgetFeedbackService feedbackService;
    private final UserRepository userRepository;
    private final CacheManager cacheManager;

    @Scheduled(cron = "0 0 0 * * *")  // 매일 자정
    public void refreshFeedbackCache() {
        log.info("budgetFeedback 캐시 갱신 시작");

        // NPE 방지를 위한 안전한 캐시 초기화
        Cache cache = cacheManager.getCache("budgetFeedback");
        if (cache != null) {
            cache.clear();
            log.debug("기존 budgetFeedback 캐시 삭제 완료");
        } else {
            log.warn("budgetFeedback 캐시를 찾을 수 없습니다");
        }

        // 모든 사용자의 캐시 갱신
        userRepository.findAll().forEach(user -> {
            try {
                feedbackService.getFeedback(user.getUserId());
                log.debug("사용자 {} budgetFeedback 캐시 갱신 완료", user.getUserId());
            } catch (Exception e) {
                log.error("사용자 {} budgetFeedback 캐시 갱신 실패: {}", user.getUserId(), e.getMessage(), e);
            }
        });

        log.info("budgetFeedback 캐시 갱신 완료");
    }
}