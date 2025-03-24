package com.locket.payment.service.pay;

import com.locket.kafka.event.PaymentSuccessEvent;
import com.locket.payment.domain.pay.dto.PaymentRequest;
import com.locket.payment.domain.pay.dto.PaymentResponse;
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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
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
    private final WalletRepository walletRepository;
    private final PaymentProducer paymentProducer;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public ResponseEntity<PaymentResponse> processPayment(PaymentRequest request) {
        // 1️⃣ 카드 정보 조회
        CardInfo cardInfo = cardInfoRepository.findByCardNumber(request.getCardNumber())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 카드 번호입니다."));
        BankAccount bankAccount = cardInfo.getBankAccount();

        BigDecimal paymentAmount = request.getAmount();
        if (bankAccount.getBalance().compareTo(paymentAmount) < 0) {
            return ResponseEntity.badRequest().body(PaymentResponse.builder()
                    .status("FAIL")
                    .message("잔액 부족")
                    .build());
        }

        // 2️⃣ Redis에서 사용자 정보 가져오기
        String birthDate = "1998";
        String userJob = "학생";
        try {
            String redisKey = "user:" + request.getBuyerId();
            birthDate = Optional.ofNullable(redisTemplate.opsForValue().get(redisKey + ":birthDate")).orElse("1998");
            userJob = Optional.ofNullable(redisTemplate.opsForValue().get(redisKey + ":userJob")).orElse("학생");
        } catch (Exception e) {
            log.warn("Redis 연결 실패, 기본값 사용");
        }

        // 3️⃣ 결제 트랜잭션 저장
        PaymentTransaction transaction = PaymentTransaction.builder()
                .card(cardInfo)
                .account(bankAccount)
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

        // 4️⃣ 결제 주문 저장
        List<PaymentOrder> orders = List.of(
                PaymentOrder.builder()
                        .paymentTransaction(transaction)
                        .card(cardInfo)
                        .buyerAccount(bankAccount)
                        .amount(paymentAmount)
                        .paymentOrderStatus(PaymentStatus.EXECUTING)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );
        paymentOrderRepository.saveAll(orders);

        // 5️⃣ 부트페이 API 호출 (모의)
        boolean isPaymentSuccess = callBootpayAPI(request);
        if (!isPaymentSuccess) {
            transaction.updateStatus(PaymentStatus.FAIL);
            orders.forEach(order -> order.updateStatus(PaymentStatus.FAIL));
            return ResponseEntity.badRequest().body(PaymentResponse.builder()
                    .transactionId(transaction.getPaymentTransactionId().toString())
                    .status("FAIL")
                    .message("결제 실패")
                    .build());
        }

        // 6️⃣ 계좌에서 금액 차감
        bankAccount.withdraw(paymentAmount);
        bankAccountRepository.save(bankAccount);

        // 7️⃣ 상태 업데이트
        transaction.updateStatus(PaymentStatus.SUCCESS);
        orders.forEach(order -> order.updateStatus(PaymentStatus.SUCCESS));

        // 8️⃣ 판매자 지갑에 입금 처리
        Wallet sellerWallet = walletRepository.findByUserId(request.getSellerId())
                .orElseThrow(() -> new IllegalArgumentException("판매자의 지갑 정보를 찾을 수 없습니다."));

        orders.forEach(order -> {
            WalletTransaction walletTransaction = WalletTransaction.builder()
                    .wallet(sellerWallet)
                    .paymentOrder(order)
                    .amount(paymentAmount)
                    .transactionType(TransactionType.DEPOSIT)
                    .status(WalletTransactionStatus.SUCCESS)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            walletTransactionRepository.save(walletTransaction);
        });

        // 9️⃣ Kafka 메시지 전송
        List<PaymentSuccessEvent.OrderDetail> orderDetails = orders.stream()
                .map(order -> new PaymentSuccessEvent.OrderDetail(
                        order.getPaymentOrderId().toString(),
                        order.getCard().getCardNumber(),
                        order.getAmount(),
                        order.getPaymentOrderStatus().name()
                )).collect(Collectors.toList());

        PaymentSuccessEvent event = PaymentSuccessEvent.builder()
                .transactionId(UUID.randomUUID().toString())
                .buyerId(request.getBuyerId())
                .sellerId(request.getSellerId())
                .userJob(userJob)
                .birthDate(birthDate)
                .totalAmount(paymentAmount)
                .currency("KRW")
                .paymentCategory(request.getPaymentCategory())
                .paymentMerchant(request.getPaymentMerchant())
                .storeName(request.getStoreName())
                .receiptUploaded(false) // 추후 true로 설정
                .paymentStatus("SUCCESS")
                .createdAt(OffsetDateTime.now(ZoneOffset.ofHours(9)))
                .orders(orderDetails)
                .build();

        paymentProducer.sendPaymentSuccessEvent(event);

        return ResponseEntity.ok(PaymentResponse.builder()
                .transactionId(transaction.getPaymentTransactionId().toString())
                .status("SUCCESS")
                .message("결제 성공 및 Kafka 메시지 전송 완료")
                .build());
    }

    private boolean callBootpayAPI(PaymentRequest request) {
        // TODO: 실제 부트페이 결제 API 연동 예정
        return true; // 현재는 무조건 성공 처리
    }
}
