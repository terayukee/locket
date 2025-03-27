package com.locket.elasticsearch.service.payment;

import com.locket.elasticsearch.domain.payment.entity.PaymentHistory;
import com.locket.elasticsearch.domain.payment.repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentQueryService {

    private final PaymentHistoryRepository paymentHistoryRepository;

    public List<PaymentHistory> findByUserAndMonth(int userId, int year, int month) {
        Instant start = LocalDate.of(year, month, 1).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = LocalDate.of(year, month, 1)
                .withDayOfMonth(LocalDate.of(year, month, 1).lengthOfMonth())
                .atTime(23, 59, 59).toInstant(ZoneOffset.UTC);

        return paymentHistoryRepository.findByBuyerIdAndCreatedAtBetween(userId, start, end);
    }
}
