package com.locket.user.service.notification;

import com.locket.user.domain.auth.entity.User;
import com.locket.user.domain.auth.repository.UserRepository;
import com.locket.user.domain.notification.dto.FcmMessageDto;
import com.locket.user.domain.product.entity.Product;
import com.locket.user.domain.product.entity.ProductUserPreference;
import com.locket.user.domain.product.repository.ProductUserPreferenceRepository;
import com.locket.user.domain.productalert.dto.ProductAlertDto;
import com.locket.user.service.productalert.ProductAlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductAlertNotificationService {

    private final ProductUserPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ProductAlertService productAlertService;

    public void notifyUsersIfPriceDrops(Product product) {
        List<ProductUserPreference> alertUsers = preferenceRepository.findByProductIdAndIsAlertTrue(product.getId());

        int price = parsePriceToInt(product.getCurrentPrice());

        for (ProductUserPreference preference : alertUsers) {
            Integer alertPrice = preference.getAlertPrice();
            if (alertPrice == null || alertPrice < price) continue;

            User user = userRepository.findById(preference.getUserId()).orElse(null);
            if (user == null || user.getFcmToken() == null) continue;

            String content = String.format("💸 [%s] 상품이 %d원 이하로 떨어졌습니다! 지금 확인해보세요.", product.getProductName(), alertPrice);

            FcmMessageDto dto = FcmMessageDto.builder()
                    .targetFcmToken(user.getFcmToken())
                    .type("price")
                    .title("📢 가격 알림 도착!")
                    .content(content)
                    .productId(product.getId())
                    .build();

            notificationService.sendBudgetAlert(dto);  // ✅ 재사용
            log.info("📲 상품 가격 알림 전송 완료: userId={}, productId={}", user.getUserId(), product.getId());

            // 알림 발송 후 is_alert(알림 발송 희망) 필드를 false로 변경
            preference.setAlert(false);
            preferenceRepository.save(preference);

            // 상품 알림 DB 저장
            productAlertService.saveAlert(ProductAlertDto.builder()
                    .userId(user.getUserId())
                    .message(content)
                    .isRead(false)
                    .alertPrice(alertPrice)
                    .productId(product.getId())
                    .build());

        }
    }

    private int parsePriceToInt(String priceStr) {
        try {
            return Integer.parseInt(priceStr.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            log.error("❌ 가격 파싱 실패: {}", priceStr, e);
            return Integer.MAX_VALUE;
        }
    }
}

