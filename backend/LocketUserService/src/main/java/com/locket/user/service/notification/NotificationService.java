package com.locket.user.service.notification;


import com.locket.user.domain.notification.dto.FcmMessageDto;

public interface NotificationService {
    void sendBudgetAlert(FcmMessageDto messageDto);
}
