package com.locket.elasticsearch.payment.repository;

import com.locket.user.domain.payment.entity.PaymentHistory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentElasticRepository extends ElasticsearchRepository<PaymentHistory, String> {
}
