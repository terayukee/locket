package com.locket.user.service.notification;

import com.locket.user.domain.goalalert.entity.GoalAlert;
import com.locket.user.domain.goalalert.repository.GoalAlertRepository;
import com.locket.user.domain.notification.dto.UserAlertDto;
import com.locket.user.domain.productalert.entity.ProductAlert;
import com.locket.user.domain.productalert.repository.ProductAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserAlertService {

    private final GoalAlertRepository goalAlertRepository;
    private final ProductAlertRepository productAlertRepository;

    public Map<String, List<UserAlertDto>> getUserAlerts(Long userId) {
        List<UserAlertDto> allAlerts = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일");

        // 🎯 목표 알림 처리
        List<GoalAlert> goalAlerts = goalAlertRepository.findByUserId(userId);
        for (GoalAlert alert : goalAlerts) {
            allAlerts.add(UserAlertDto.builder()
                    .type("goal")
                    .alertId(alert.getNotificationId())
                    .message(alert.getMessage())
                    .isRead(!alert.getIsRead())
                    .alertPrice(null)
                    .createdAt(alert.getCreatedAt())
                    .formattedDate(alert.getCreatedAt().format(formatter))  // 🆕
                    .build());
        }

        // 🛍️ 상품 알림 처리
        List<ProductAlert> productAlerts = productAlertRepository.findByUserId(userId);
        for (ProductAlert alert : productAlerts) {
            allAlerts.add(UserAlertDto.builder()
                    .type("product")
                    .alertId(alert.getId())
                    .message(alert.getMessage())
                    .isRead(!alert.getIsRead())
                    .alertPrice(alert.getAlertPrice())
                    .productId(alert.getProductId())
                    .createdAt(alert.getCreatedAt())
                    .formattedDate(alert.getCreatedAt().format(formatter))  // 🆕
                    .build());
        }

        // 🕘 최신순 정렬
        allAlerts.sort(Comparator.comparing(UserAlertDto::getCreatedAt).reversed());

        // 📅 최근 7일 기준 분류
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        Map<String, List<UserAlertDto>> grouped = new HashMap<>();
        grouped.put("recent", new ArrayList<>());
        grouped.put("past", new ArrayList<>());

        for (UserAlertDto dto : allAlerts) {
            if (dto.getCreatedAt().isAfter(sevenDaysAgo)) {
                grouped.get("recent").add(dto);
            } else {
                grouped.get("past").add(dto);
            }
        }

        return grouped;
    }

}
