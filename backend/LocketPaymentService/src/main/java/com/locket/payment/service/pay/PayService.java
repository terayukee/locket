package com.locket.payment.service.pay;

import com.locket.kafka.event.PaymentSuccessEvent;
import com.locket.payment.domain.pay.dto.QrPaymentRequest;
import com.locket.payment.domain.pay.dto.QrPaymentResponse;
import com.locket.payment.domain.pay.entity.PaymentTransaction;
import com.locket.payment.domain.pay.entity.PaymentOrder;
import com.locket.payment.domain.pay.entity.PaymentStatus;
import com.locket.payment.domain.pay.repository.PaymentTransactionRepository;
import com.locket.payment.domain.pay.repository.PaymentOrderRepository;
import com.locket.payment.infra.kafka.PaymentProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentOrderRepository paymentOrderRepository;
    private final PaymentProducer paymentProducer; // ✅ Kafka 메시지 전송을 위한 추가

    @Transactional
    public ResponseEntity<QrPaymentResponse> processQrPayment(QrPaymentRequest request) {
        // 1. 결제 트랜잭션 저장
        PaymentTransaction transaction = PaymentTransaction.builder()
                .accountId(request.getAccountId())
                .buyerId(request.getBuyerId())
                .sellerId(request.getSellerId())
                .paymentTransactionStatus(PaymentStatus.EXECUTING)
                .paymentCategory(request.getPaymentCategory())
                .paymentMerchant(request.getPaymentMerchant())
                .paymentTimestamp(LocalDateTime.now())
                .build();
        paymentTransactionRepository.save(transaction);

        // 2. 결제 주문 저장
        PaymentOrder order = PaymentOrder.builder()
                .paymentTransactionId(transaction.getPaymentTransactionId())
                .amount(request.getAmount())
                .paymentOrderStatus(PaymentStatus.EXECUTING)
                .build();
        paymentOrderRepository.save(order);

        // 3. 외부 API (부트페이) 호출
        boolean isPaymentSuccess = callBootpayAPI(request);

        if (!isPaymentSuccess) {
            transaction.updateStatus(PaymentStatus.FAIL);
            order.updateStatus(PaymentStatus.FAIL);
            return ResponseEntity.badRequest().body(QrPaymentResponse.builder()
                    .transactionId(transaction.getPaymentTransactionId().toString())
                    .status("FAIL")
                    .message("결제 실패")
                    .build());
        }

        // 4. 결제 성공 후 상태 업데이트
        transaction.updateStatus(PaymentStatus.SUCCESS);
        order.updateStatus(PaymentStatus.SUCCESS);

        // ✅ 5. 결제 성공 시 Kafka 메시지 전송
        PaymentSuccessEvent event = new PaymentSuccessEvent(
                UUID.randomUUID().toString(),  // 고유 Transaction ID 생성
                request.getAccountId(),
                request.getBuyerId(),
                request.getSellerId(),
                request.getPaymentCategory(),
                request.getPaymentMerchant()
        );
        paymentProducer.sendPaymentSuccessEvent(event);

        // ✅ 6. 최종 응답 반환
        return ResponseEntity.ok(QrPaymentResponse.builder()
                .transactionId(transaction.getPaymentTransactionId().toString())
                .status("SUCCESS")
                .message("결제 성공 및 Kafka 메시지 전송 완료")
                .build());
    }

    private boolean callBootpayAPI(QrPaymentRequest request) {
        // TODO: 부트페이 API 호출 로직 추가
        return true; // 테스트용 (성공)
    }
}
