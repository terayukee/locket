package com.locket.user.service.notification;

import com.locket.kafka.event.PaymentSuccessEvent;
import com.locket.user.domain.auth.entity.User;
import com.locket.user.domain.auth.repository.UserRepository;
import com.locket.user.domain.notification.dto.FcmMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentNotificationService {

    private final UserRepository userRepository;
    private final FirebaseSendService firebaseSendService;

    public void notifyPaymentComplete(PaymentSuccessEvent event) {
        Long userId = event.getBuyerId();

        // 유저 정보 조회
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getFcmToken() == null) {
            log.warn("❌ FCM 알림 스킵: 사용자 정보 없음 또는 FCM 토큰 없음 (userId={})", userId);
            return;
        }

        String title = "💰 결제 완료 알림";
        String content = String.format("[%s] %d원 결제가 완료되었습니다.", event.getStoreName(), event.getTotalAmount().longValue());

        FcmMessageDto dto = FcmMessageDto.builder()
                .targetFcmToken(user.getFcmToken())
                .title(title)
                .content(content)
                .type("payment")
                .build();

        firebaseSendService.sendFcmNotification(dto);
        log.info("📨 결제 완료 FCM 알림 전송 완료 (userId={}, amount={})", userId, event.getTotalAmount().longValue());
    }
}
