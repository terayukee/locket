package com.locket.elasticsearch.domain.payment.repository;

import com.locket.elasticsearch.domain.payment.entity.PaymentHistory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PaymentHistoryRepository extends ElasticsearchRepository<PaymentHistory, String> {

    List<PaymentHistory> findByBuyerIdAndYearAndMonth(long buyerId, int year, int month);
}
