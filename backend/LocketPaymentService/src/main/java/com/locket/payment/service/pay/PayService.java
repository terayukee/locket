package com.locket.payment.service.pay;

import com.locket.kafka.event.PaymentSuccessEvent;
import com.locket.payment.domain.pay.dto.QrPaymentRequest;
import com.locket.payment.domain.pay.dto.QrPaymentResponse;
import com.locket.payment.domain.pay.entity.*;
import com.locket.payment.domain.pay.repository.*;
import com.locket.payment.infra.kafka.PaymentProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentOrderRepository paymentOrderRepository;
    private final BankAccountRepository bankAccountRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final CardInfoRepository cardInfoRepository;
    private final PaymentProducer paymentProducer;
    private final StringRedisTemplate redisTemplate; // ✅ Redis 추가

    @Transactional
    public ResponseEntity<QrPaymentResponse> processQrPayment(QrPaymentRequest request) {
        // 1️⃣ 카드 정보 조회 (카드 번호 → 카드 ID & 은행 계좌 ID)
        CardInfo cardInfo = cardInfoRepository.findByCardNumber(request.getCardNumber())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 카드 번호입니다."));

        BankAccount bankAccount = cardInfo.getBankAccount();

        BigDecimal paymentAmount = request.getAmount();
        if (bankAccount.getBalance().compareTo(paymentAmount) < 0) {
            return ResponseEntity.badRequest().body(QrPaymentResponse.builder()
                    .status("FAIL")
                    .message("잔액 부족")
                    .build());
        }

        // 2️⃣ Redis에서 사용자 정보 가져오기
        String birthDate = "1998";
        String userJob = "학생";

        try {
            String redisKey = "user:" + request.getBuyerId();
            birthDate = redisTemplate.opsForValue().get(redisKey + ":birthDate");
            userJob = redisTemplate.opsForValue().get(redisKey + ":userJob");
        } catch (Exception e) {
            log.warn("Redis 연결 실패, 기본값 사용");
        }

        // 3️⃣ 결제 트랜잭션 저장
        PaymentTransaction transaction = PaymentTransaction.builder()
                .card(cardInfo)
                .buyerId(request.getBuyerId())
                .sellerId(request.getSellerId())
                .paymentTransactionStatus(PaymentStatus.EXECUTING)
                .paymentCategory(request.getPaymentCategory())
                .paymentMerchant(request.getPaymentMerchant())
                .amount(paymentAmount)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        paymentTransactionRepository.save(transaction);

        // 4️⃣ 결제 주문 저장 (리스트 형태)
        List<PaymentOrder> orders = List.of(
                PaymentOrder.builder()
                        .paymentTransaction(transaction)
                        .card(cardInfo)
                        .amount(paymentAmount)
                        .paymentOrderStatus(PaymentStatus.EXECUTING)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );
        paymentOrderRepository.saveAll(orders);

        // 5️⃣ 부트페이 API 호출 (실제 결제 진행)
        boolean isPaymentSuccess = callBootpayAPI(request);

        if (!isPaymentSuccess) {
            transaction.updateStatus(PaymentStatus.FAIL);
            orders.forEach(order -> order.updateStatus(PaymentStatus.FAIL));
            return ResponseEntity.badRequest().body(QrPaymentResponse.builder()
                    .transactionId(transaction.getPaymentTransactionId().toString())
                    .status("FAIL")
                    .message("결제 실패")
                    .build());
        }

        // 6️⃣ 결제 성공 → 계좌 잔액 차감 (동시성 문제 방지)
        bankAccount.withdraw(paymentAmount);
        bankAccountRepository.save(bankAccount);

        // 7️⃣ 결제 상태 업데이트
        transaction.updateStatus(PaymentStatus.SUCCESS);
        orders.forEach(order -> order.updateStatus(PaymentStatus.SUCCESS));

        // 8️⃣ 판매자 지갑에 금액 추가
        WalletTransaction walletTransaction = WalletTransaction.builder()
                .walletId(request.getSellerId())
                .amount(paymentAmount)
                .transactionType(TransactionType.DEPOSIT)
                .status(WalletTransactionStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        walletTransactionRepository.save(walletTransaction);

        // 9️⃣ Kafka 이벤트 발행 (결제 성공)
        List<PaymentSuccessEvent.OrderDetail> orderDetails = orders.stream()
                .map(order -> new PaymentSuccessEvent.OrderDetail(
                        order.getPaymentOrderId().toString(),
                        order.getCard().getCardNumber(),
                        order.getAmount(),
                        order.getPaymentOrderStatus().name()
                )).collect(Collectors.toList());

        PaymentSuccessEvent event = new PaymentSuccessEvent(
                UUID.randomUUID().toString(),
                request.getBuyerId(),
                request.getSellerId(),
                userJob, // ✅ Redis에서 가져온 값 포함
                birthDate, // ✅ Redis에서 가져온 값 포함
                paymentAmount,
                "KRW",
                request.getPaymentCategory(),
                request.getPaymentMerchant(),
                "SUCCESS",
                LocalDateTime.now(),
                orderDetails
        );

        paymentProducer.sendPaymentSuccessEvent(event);

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
