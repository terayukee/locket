package com.locket.elasticsearch.service.payment;

import com.locket.elasticsearch.domain.payment.dto.ReceiptUpdateRequest;
import com.locket.elasticsearch.domain.payment.entity.PaymentHistory;
import com.locket.elasticsearch.domain.payment.repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final PaymentHistoryRepository paymentHistoryRepository;

    public void updateReceiptInfo(ReceiptUpdateRequest request) {
        // 1. 결제 내역 존재 여부 확인
        PaymentHistory payment = paymentHistoryRepository.findById(request.getTransactionId())
                .orElseThrow(() -> new RuntimeException("결제 내역을 찾을 수 없습니다: " + request.getTransactionId()));

        // 2. 이미 영수증이 등록되었는지 확인
        if (payment.isReceiptUploaded()) {
            throw new RuntimeException("이미 영수증이 등록된 결제 내역입니다: " + request.getTransactionId());
        }

        // 3. OCR 결과 저장
        payment.setReceiptItems(convertToReceiptItems(request.getItems()));
        payment.setCategoryAmount(request.getCategoryAmount());
        payment.setReceiptUploaded(true);

        // 4. 저장
        paymentHistoryRepository.save(payment);
        log.info("영수증 정보가 업데이트되었습니다. transactionId: {}", request.getTransactionId());
    }

    private List<PaymentHistory.ReceiptItem> convertToReceiptItems(List<ReceiptUpdateRequest.ReceiptItem> items) {
        return items.stream()
                .map(item -> PaymentHistory.ReceiptItem.builder()
                        .itemId(item.getItemId())
                        .itemName(item.getItemName())
                        .itemQuantity(item.getItemQuantity())
                        .itemAmount(item.getItemAmount())
                        .itemCategory(item.getItemCategory())
                        .build())
                .collect(Collectors.toList());
    }
}