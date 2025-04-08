package com.locket.payment.service.pay;

import com.locket.kafka.event.PaymentSuccessEvent;
import com.locket.payment.domain.pay.dto.*;
import com.locket.payment.domain.pay.entity.*;
import com.locket.payment.domain.pay.repository.*;
import com.locket.payment.feign.PaymentHistoryFeignClient;
import com.locket.payment.infra.kafka.PaymentProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;
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
    private final PaymentLedgerRepository paymentLedgerRepository;
    private final PaymentProducer paymentProducer;
    private final StringRedisTemplate redisTemplate;
    private final PaymentHistoryFeignClient paymentHistoryFeignClient;


    /**
     * 카드 유효성 및 잔액 확인
     */
    public ResponseEntity<Map<String, String>> validateCardAndBalance(int cardId, BigDecimal amount) {
        Map<String, String> response = new HashMap<>();
        try {
            // 1️⃣ 카드 정보 조회
            CardInfo cardInfo = cardInfoRepository.findByCardId(cardId)
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 카드 번호입니다."));

            BankAccount bankAccount = cardInfo.getBankAccount();


            // 2️⃣ 잔액 확인
            if (bankAccount.getBalance().compareTo(amount) < 0) {
                response.put("status", "FAIL");
                response.put("message", "잔액이 부족합니다.");
                return ResponseEntity.badRequest().body(response);
            }

            response.put("status", "OK");
            response.put("message", "카드 유효 및 잔액 충분");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "FAIL");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Transactional
    public ResponseEntity<PaymentResponse> processPayment(PaymentRequest request) {
        // 카드 유효성 및 잔액 확인
        validateCardAndBalance(request.getCardId(), request.getAmount());

        try {
            // ✅ 중복 결제 방지 - paymentKey Redis에 체크
            String redisPaymentKey = "payment:dup:" + request.getPaymentKey();
            Boolean exists = redisTemplate.hasKey(redisPaymentKey);

            if (Boolean.TRUE.equals(exists)) {
                return ResponseEntity.status(409).body(
                        PaymentResponse.builder()
                                .transactionId(null)
                                .status("DUPLICATE_PAYMENT")
                                .message("이미 처리된 결제 요청입니다.")
                                .build()
                );
            }

            // ✅ Redis에 결제 키 등록 (유효 시간 예: 5초)
            redisTemplate.opsForValue().set(redisPaymentKey, "LOCK", Duration.ofSeconds(5));

            // 카드 정보
            int cardId = request.getCardId();
            CardInfo cardInfo = cardInfoRepository.findByCardId(cardId)
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 카드입니다."));

            // ✅ 락을 걸고 계좌 조회
            BankAccount bankAccount = bankAccountRepository.findByAccountId(cardInfo.getBankAccount().getAccountId())
                    .orElseThrow(() -> new IllegalArgumentException("계좌 정보를 찾을 수 없습니다."));
//        BankAccount bankAccount = cardInfo.getBankAccount();

            if (bankAccount == null) {
                return ResponseEntity.badRequest().body(
                        PaymentResponse.builder()
                                .transactionId(null)
                                .status("BAD_REQUEST")
                                .message("카드에 연결된 계좌 정보가 없습니다.")
                                .build()
                );
            }

            // 결제 금액
            BigDecimal paymentAmount = request.getAmount();

            if (bankAccount.getBalance().compareTo(paymentAmount) < 0) {
                return ResponseEntity.status(402).body(
                        PaymentResponse.builder()
                                .transactionId(null)
                                .status("PAYMENT_REQUIRED")
                                .message("잔액 부족")
                                .build()
                );
            }

            // 1️⃣Redis에서 사용자 정보 가져오기
            long buyerId = cardInfo.getUserId();

            int birthYear = 1998;
            String userJob = "학생";

            try {
                String redisKey = "user:" + buyerId + ":auth";

                Object birthYearValue = redisTemplate.opsForHash().get(redisKey, "birthYear");
                Object userJobValue = redisTemplate.opsForHash().get(redisKey, "userJob");

                if (birthYearValue != null) {
                    birthYear = Integer.parseInt(birthYearValue.toString());
                } else {
                    log.warn("❗ Redis에서 birthYear 값을 찾을 수 없음. 기본값 사용: {}", birthYear);
                }

                if (userJobValue != null) {
                    userJob = userJobValue.toString();
                } else {
                    log.warn("❗ Redis에서 userJob 값을 찾을 수 없음. 기본값 사용: {}", userJob);
                }

            } catch (Exception e) {
                log.warn("❌ Redis 사용자 정보 조회 실패: {} - 기본값 사용 (birthYear={}, userJob={})", e.getMessage(), birthYear, userJob);
            }

            // 2️⃣  결제 트랜잭션 저장
            PaymentTransaction transaction = PaymentTransaction.builder()
                    .card(cardInfo)
                    .account(bankAccount)
                    .buyerId(buyerId)
                    .sellerId(request.getSellerId())
                    .paymentTransactionStatus(PaymentStatus.EXECUTING)
                    .paymentCategory(request.getPaymentCategory())
                    .paymentMerchant(request.getPaymentMerchant())
                    .amount(paymentAmount)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            paymentTransactionRepository.save(transaction);

            // 3️⃣ 결제 주문 저장
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

            // 4️⃣ 부트페이 API 호출 - 결제 검증하기 (모의)
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

            // 5️⃣ 계좌에서 금액 차감
            bankAccount.withdraw(paymentAmount);
            bankAccountRepository.save(bankAccount);

            // 6️⃣ 상태 업데이트
            transaction.updateStatus(PaymentStatus.SUCCESS);
            orders.forEach(order -> order.updateStatus(PaymentStatus.SUCCESS));

            // 7️⃣ [비관적 락] 지갑 트랜잭션 저장 & 원장 기록 & 판매자 지갑에 입금 처리
            Wallet sellerWallet = walletRepository.findByUserIdForUpdate(request.getSellerId())
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

                // 8️⃣ 원장 기록 추가
                PaymentLedger ledger = PaymentLedger.builder()
                        .paymentOrder(order)
                        .amount(order.getAmount())
                        .currency("KRW")
                        .debitAccount(bankAccount.getAccountNumber())
                        .creditAccount(sellerWallet.getWalletId().toString())
                        .build();
                paymentLedgerRepository.save(ledger);
            });

            sellerWallet.deposit(paymentAmount); // ✅ balance 증가
            walletRepository.save(sellerWallet); // ✅ 저장

            // 9️⃣ Kafka 메시지 전송
            List<PaymentSuccessEvent.OrderDetail> orderDetails = orders.stream()
                    .map(order -> new PaymentSuccessEvent.OrderDetail(
                            order.getPaymentOrderId().toString(),
                            order.getCard().getCardNumber(),
                            order.getAmount(),
                            order.getPaymentOrderStatus().name()
                    )).collect(Collectors.toList());

            OffsetDateTime createdAt = OffsetDateTime.now(ZoneOffset.ofHours(9));

            PaymentSuccessEvent event = PaymentSuccessEvent.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .buyerId(buyerId)
                    .sellerId(request.getSellerId())
                    .userJob(userJob)
                    .birthDate(birthYear)
                    .totalAmount(paymentAmount)
                    .currency("KRW")
                    .cardId(cardId)
                    .cardName(cardInfo.getCardName())
                    .paymentMerchant(request.getPaymentMerchant())
                    .storeName(request.getStoreName())
                    .receiptUploaded(false) // 추후 true로 설정
                    .paymentStatus("SUCCESS")
                    .createdAt(createdAt)
                    .year(createdAt.getYear())
                    .month(createdAt.getMonthValue())
                    .day(createdAt.getDayOfMonth())
                    .orders(orderDetails)
                    .build();

            paymentProducer.sendPaymentSuccessEvent(event);

            return ResponseEntity.ok(PaymentResponse.builder()
                    .transactionId(transaction.getPaymentTransactionId().toString())
                    .status("SUCCESS")
                    .message("결제 성공 및 Kafka 메시지 전송 완료")
                    .build());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    PaymentResponse.builder()
                            .transactionId(null)
                            .status("BAD_REQUEST")
                            .message(e.getMessage())
                            .build()
            );
        } catch (OptimisticLockingFailureException e) {
            log.warn("💥 낙관적 락 충돌 발생 - 동시 수정 감지됨", e);
            return ResponseEntity.status(409).body(
                    PaymentResponse.builder()
                            .transactionId(null)
                            .status("CONFLICT")
                            .message("다른 요청이 동시에 처리되어 충돌이 발생했습니다. 다시 시도해주세요.")
                            .build()
            );
        } catch (Exception e) {
            log.error("[결제 실패 - 시스템 오류]", e);
            return ResponseEntity.status(500).body(
                    PaymentResponse.builder()
                            .transactionId(null)
                            .status("INTERNAL_ERROR")
                            .message("서버 내부 오류")
                            .build()
            );
        }
    }

    private boolean callBootpayAPI(PaymentRequest request) {
        // TODO: 실제 부트페이 결제 API 연동 예정
        return true; // 현재는 무조건 성공 처리
    }

    public List<CardInfoDto> getCardsWithMonthlyUsage(long userId) {
        log.info("➡️ 카드 조회 시작: userId = {}", userId);

        List<CardInfo> cards = cardInfoRepository.findByUserId(userId);
        log.info("🔢 사용자 카드 개수: {}", cards.size());

        if (cards.isEmpty()) {
            throw new NoSuchElementException("해당 사용자에게 등록된 카드가 없습니다.");
        }

        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        return cards.stream()
                .map(card -> {
                    log.info("🧾 카드 정보 - cardId: {}, cardName: {}", card.getCardId(), card.getCardName());

                    BigDecimal monthlyUsage = BigDecimal.ZERO;

                    try {
                        CardMonthlyUsageDto usageDto = paymentHistoryFeignClient
                                .getMonthlyTotalAmountByCard(userId, card.getCardId(), year, month);
                        monthlyUsage = usageDto.getTotalAmount();
                        log.info("📊 월간 사용금액 조회 완료 - cardId: {}, usage: {}", card.getCardId(), monthlyUsage);
                    } catch (Exception e) {
                        log.error("💥 Feign 호출 실패 - 카드 ID: {}, 에러: {}", card.getCardId(), e.getMessage());
                        throw new RuntimeException("월간 사용금액 조회 실패: cardId=" + card.getCardId(), e);
                    }

                    List<CardBenefitDto> benefits = new ArrayList<>();
                    if (card.getCardCatalog() != null) {
                        log.info("🎁 카드 혜택 있음 - cardId: {}", card.getCardId());
                        List<CardBenefit> benefitEntities = card.getCardCatalog().getBenefits();
                        if (benefitEntities != null) {
                            benefits = benefitEntities.stream()
                                    .map(b -> CardBenefitDto.builder()
                                            .benefitId(b.getBenefitId())
                                            .item(b.getItem())
                                            .benefitDetail(b.getBenefitDetail())
                                            .build())
                                    .collect(Collectors.toList());
                            log.info("✅ 혜택 {}건 추가됨", benefits.size());
                        }
                    }

                    return CardInfoDto.builder()
                            .cardId(card.getCardId())
                            .userId(card.getUserId())
                            .cardNumber(card.getCardNumber())
                            .cardExpiry(card.getCardExpiry())
                            .cardCvc(card.getCardCvc())
                            .cardName(card.getCardName())
                            .accountNumber(card.getBankAccount().getAccountNumber())
                            .createdAt(card.getCreatedAt())
                            .updatedAt(card.getUpdatedAt())
                            .monthlyUsage(monthlyUsage)
                            .benefits(benefits)
                            .build();
                })
                .collect(Collectors.toList());
    }



    public boolean getFingerprintRegisteredFromRedis(long userId) {
        String key = "user:" + userId + ":auth";

        try {
            Object rawValue = redisTemplate.opsForHash().get(key, "fingerprintRegistered");

            if (rawValue == null) {
                throw new NoSuchElementException("지문 등록 정보가 존재하지 않습니다.");
            }

            return Boolean.parseBoolean(rawValue.toString());
        } catch (Exception e) {
            // Redis 연결 문제 or 형식 오류 등
            throw new IllegalStateException("지문 등록 여부 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public boolean verifyPaymentPassword(PaymentPasswordRequest passwordRequest) {
        long userId = passwordRequest.getUserId();
        String inputPassword = passwordRequest.getPaymentPassword();
        log.info("입력한 결제 PW : {}", inputPassword);

        String key = "user:" + userId + ":auth";

        try {
            Object value = redisTemplate.opsForHash().get(key, "paymentPassword");

            if (value == null) {
                throw new NoSuchElementException("등록된 간편 비밀번호가 없습니다.");
            }

            String storedPassword = value.toString();
            log.info("저장된 결제 PW : {}, 입력한 결제 PW : {}", storedPassword, inputPassword);

            return storedPassword.equals(inputPassword);

        } catch (NumberFormatException e) {
            throw new IllegalStateException("Redis에 저장된 비밀번호 형식이 올바르지 않습니다.");
        } catch (Exception e) {
            throw new IllegalStateException("비밀번호 검증 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public BigDecimal getMonthlyTotalAmount(long userId, int year, int month) {
        List<MonthPaymentDto> paymentList = paymentHistoryFeignClient
                .getMonthPayments(userId, year, month)
                .getPayments(); // ✅ 추가로 .getPayments() 호출 필요

        return paymentList.stream()
                .map(MonthPaymentDto::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
