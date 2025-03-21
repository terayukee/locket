package com.locket.elasticsearch.payment.repository;

import com.locket.elasticsearch.payment.entity.PaymentHistory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentElasticRepository extends ElasticsearchRepository<PaymentHistory, String> {
    List<PaymentHistory> findByBuyerIdAndCreatedAtBetween(int buyerId, LocalDateTime from, LocalDateTime to);

}
