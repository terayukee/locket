package com.locket.user.service.productalert;

import com.locket.user.domain.productalert.dto.ProductAlertDto;
import com.locket.user.domain.productalert.entity.ProductAlert;
import com.locket.user.domain.productalert.repository.ProductAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductAlertService {

    private final ProductAlertRepository productAlertRepository;

    public void saveAlert(ProductAlertDto dto) {
        ProductAlert alert = ProductAlert.builder()
                .userId(dto.getUserId())
                .message(dto.getMessage())
                .isAlert(dto.getIsAlert())
                .alertPrice(dto.getAlertPrice())
                .build();

        productAlertRepository.save(alert);
    }
}