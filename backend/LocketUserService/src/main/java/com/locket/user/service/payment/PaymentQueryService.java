package com.locket.user.service.payment;

import com.locket.user.domain.payment.dto.PaymentHistoryDto;
import com.locket.user.feign.PaymentHistoryFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentQueryService {

    private final PaymentHistoryFeignClient paymentHistoryFeignClient;

    public List<PaymentHistoryDto> getUserPaymentHistory(int userId) {
        return paymentHistoryFeignClient.getPaymentHistories(userId);
    }
}