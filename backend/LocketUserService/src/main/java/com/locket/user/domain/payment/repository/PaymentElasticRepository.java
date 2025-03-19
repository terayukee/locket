package com.locket.user.domain.payment.repository;

import com.locket.kafka.event.PaymentSuccessEvent;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentElasticRepository extends ElasticsearchRepository<PaymentSuccessEvent, String> {
}
