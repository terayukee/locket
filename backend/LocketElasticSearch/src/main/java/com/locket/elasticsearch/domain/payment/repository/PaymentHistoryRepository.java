package com.locket.elasticsearch.domain.payment.repository;

import com.locket.elasticsearch.domain.payment.entity.PaymentHistory;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;


@Repository
public interface PaymentHistoryRepository extends ElasticsearchRepository<PaymentHistory, String> {

    List<PaymentHistory> findByBuyerIdAndYearAndMonth(long buyerId, int year, int month);

    List<PaymentHistory> findByBuyerIdAndYearAndMonthAndDay(long buyerId, int year, int month, int day);

    // 기본적으로 자동으로 파싱해서 날짜 range 쿼리 수행해줌
    List<PaymentHistory> findByBuyerIdAndCreatedAtBetween(long buyerId, Instant from, Instant to);


    // 영수증 등록 가능한 전체 결제 내역 조회
    List<PaymentHistory> findByBuyerIdAndPaymentStatusAndReceiptUploaded(
            long buyerId,
            String paymentStatus,
            boolean receiptUploaded
    );


    // 사용자의 전체 결제 내역 조회
    List<PaymentHistory> findByBuyerId(long buyerId);


    @Query("{\"bool\": {\"must\": [" +
            "{\"term\": {\"buyerId\": ?0}}," +
            "{\"term\": {\"year\": ?1}}," +
            "{\"term\": {\"month\": ?2}}" +
            "]}}")
    List<PaymentHistory> findByBuyerIdAndYearAndMonthCustomQuery(long buyerId, int year, int month);

    // 한 카드의 이번달 결제 내역
    List<PaymentHistory> findByBuyerIdAndCardIdAndYearAndMonth(Long buyerId, Integer cardId, int year, int month);

}
