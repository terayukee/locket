package com.locket.user.service.notification;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.locket.user.domain.notification.dto.FcmMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final FirebaseMessaging firebaseMessaging;

    public void sendBudgetAlert(FcmMessageDto dto) {
        try {
            Message message = Message.builder()
                    .setToken(dto.getTargetFcmToken())
                    .putData("title", dto.getTitle())
                    .putData("body", dto.getContent())
                    .build();

            firebaseMessaging.send(message);
            log.info("✅ FCM 알림 전송 완료: {}", dto);

        } catch (Exception e) {
            log.error("❌ FCM 알림 전송 실패: {}", e.getMessage(), e);
        }
    }
}
