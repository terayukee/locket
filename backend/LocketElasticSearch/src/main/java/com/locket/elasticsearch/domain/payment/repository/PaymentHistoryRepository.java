package com.locket.elasticsearch.domain.payment.repository;

import com.locket.elasticsearch.domain.payment.entity.PaymentHistory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PaymentHistoryRepository extends ElasticsearchRepository<PaymentHistory, String> {

    List<PaymentHistory> findByBuyerIdAndYearAndMonth(long buyerId, int year, int month);

    List<PaymentHistory> findByBuyerIdAndYearAndMonthAndDay(long buyerId, int year, int month, int day);

    // 기본적으로 자동으로 파싱해서 날짜 range 쿼리 수행해줌
    List<PaymentHistory> findByBuyerIdAndCreatedAtBetween(int buyerId, Instant from, Instant to);

    // 영수증 등록 가능한 결제 내역 조회
    List<PaymentHistory> findByBuyerIdAndPaymentStatusAndReceiptUploaded(
            int buyerId,
            String paymentStatus,
            boolean receiptUploaded
    );

}
