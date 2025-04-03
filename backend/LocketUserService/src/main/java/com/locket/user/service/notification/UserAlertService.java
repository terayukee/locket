package com.locket.user.service.notification;

import com.locket.user.domain.goalalert.entity.GoalAlert;
import com.locket.user.domain.goalalert.repository.GoalAlertRepository;
import com.locket.user.domain.notification.dto.UserAlertDto;
import com.locket.user.domain.productalert.entity.ProductAlert;
import com.locket.user.domain.productalert.repository.ProductAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAlertService {

    private final GoalAlertRepository goalAlertRepository;
    private final ProductAlertRepository productAlertRepository;

    public List<UserAlertDto> getUserAlerts(Long userId) {
        List<UserAlertDto> result = new ArrayList<>();

        // 🎯 목표 알림 처리
        List<GoalAlert> goalAlerts = goalAlertRepository.findByUserId(userId);
        for (GoalAlert alert : goalAlerts) {
            result.add(UserAlertDto.builder()
                    .type("goal")
                    .alertId(alert.getNotificationId())
                    .message(alert.getMessage())
                    .isRead(!alert.getIsRead())  // 읽지 않았으면 알림으로 표시
                    .alertPrice(null)
                    .createdAt(alert.getCreatedAt())
                    .build());
        }

        // 🛍️ 상품 알림 처리
        List<ProductAlert> productAlerts = productAlertRepository.findByUserId(userId);
        for (ProductAlert alert : productAlerts) {
            result.add(UserAlertDto.builder()
                    .type("product")
                    .alertId(alert.getId())
                    .message(alert.getMessage())
                    .isRead(!alert.getIsRead())  // 동일하게 반영
                    .alertPrice(alert.getAlertPrice())
                    .createdAt(alert.getCreatedAt())
                    .build());
        }

        // 🕘 최신순 정렬
        result.sort(Comparator.comparing(UserAlertDto::getCreatedAt).reversed());

        return result;
    }
}
