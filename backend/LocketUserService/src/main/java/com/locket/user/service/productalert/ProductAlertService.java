package com.locket.user.service.productalert;

import com.locket.user.domain.productalert.dto.ProductAlertDto;
import com.locket.user.domain.productalert.entity.ProductAlert;
import com.locket.user.domain.productalert.repository.ProductAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductAlertService {

    private final ProductAlertRepository productAlertRepository;

    public void saveAlert(ProductAlertDto dto) {
        if (dto.getAlertPrice() == null || dto.getAlertPrice() <= 0) {
            throw new IllegalArgumentException("알림 설정 금액은 0원보다 커야 합니다.");
        }

        ProductAlert alert = ProductAlert.builder()
                .userId(dto.getUserId())
                .message(dto.getMessage())
                .isRead(dto.getIsRead())
                .alertPrice(dto.getAlertPrice())
                .productId(dto.getProductId())
                .createdAt(LocalDateTime.now())
                .build();

        productAlertRepository.save(alert);
    }
}