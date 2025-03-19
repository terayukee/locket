package com.locket.payment.service.pay;

import com.locket.kafka.event.PaymentSuccessEvent;
import com.locket.payment.domain.pay.dto.QrPaymentRequest;
import com.locket.payment.domain.pay.dto.QrPaymentResponse;
import com.locket.payment.domain.pay.entity.*;
import com.locket.payment.domain.pay.repository.*;
import com.locket.payment.infra.kafka.PaymentProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentOrderRepository paymentOrderRepository;
    private final BankAccountRepository bankAccountRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final CardInfoRepository cardInfoRepository;
    private final PaymentProducer paymentProducer;

    @Transactional
    public ResponseEntity<QrPaymentResponse> processQrPayment(QrPaymentRequest request) {
        // 1️⃣ 카드 정보 조회 (카드 번호 → 카드 ID & 은행 계좌 ID)
        CardInfo cardInfo = cardInfoRepository.findByCardNumber(request.getCardNumber())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 카드 번호입니다."));

        BankAccount bankAccount = bankAccountRepository.findById(cardInfo.getBankAccountId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 계좌입니다."));

        BigDecimal paymentAmount = request.getAmount();
        if (bankAccount.getBalance().compareTo(paymentAmount) < 0) {
            return ResponseEntity.badRequest().body(QrPaymentResponse.builder()
                    .status("FAIL")
                    .message("잔액 부족")
                    .build());
        }

        // 3️⃣ 결제 트랜잭션 저장
        PaymentTransaction transaction = PaymentTransaction.builder()
                .card(cardInfo) // ✅ 객체 참조로 변경
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
        PaymentOrder order = PaymentOrder.builder()
                .paymentTransaction(transaction) // ✅ 객체 참조로 변경
                .card(cardInfo) // ✅ 객체 참조로 변경
                .amount(paymentAmount)
                .paymentOrderStatus(PaymentStatus.EXECUTING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        paymentOrderRepository.save(order);

        // 5️⃣ 부트페이 API 호출 (실제 결제 진행)
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

        // 6️⃣ 결제 성공 → 계좌 잔액 차감 (동시성 문제 방지)
        bankAccount.withdraw(paymentAmount);
        bankAccountRepository.save(bankAccount);

        // 7️⃣ 결제 상태 업데이트
        transaction.updateStatus(PaymentStatus.SUCCESS);
        order.updateStatus(PaymentStatus.SUCCESS);

        // 8️⃣ 판매자 지갑에 금액 추가
        WalletTransaction walletTransaction = WalletTransaction.builder()
                .walletId(request.getSellerId()) // 판매자 ID
                .amount(paymentAmount)
                .transactionType(TransactionType.DEPOSIT)
                .status(WalletTransactionStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        walletTransactionRepository.save(walletTransaction);

        // 9️⃣ Kafka 이벤트 발행 (결제 성공)
        PaymentSuccessEvent event = new PaymentSuccessEvent(
                UUID.randomUUID().toString(),
                cardInfo.getBankAccountId(),
                request.getBuyerId(),
                request.getSellerId(),
                request.getPaymentCategory(),
                request.getPaymentMerchant()
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
