package com.locket.user.service.payment.elastic;

import com.locket.kafka.event.PaymentSuccessEvent;
import com.locket.user.domain.payment.repository.PaymentElasticRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticSearchService {

    private final PaymentElasticRepository paymentElasticRepository;

    public void indexPaymentEvent(PaymentSuccessEvent event) {
        log.info("🔍 Indexing Payment Event in ElasticSearch: {}", event);
        paymentElasticRepository.save(event);
    }

    public Optional<PaymentSuccessEvent> getPaymentEventById(String transactionId) {
        return paymentElasticRepository.findById(transactionId);
    }
}
