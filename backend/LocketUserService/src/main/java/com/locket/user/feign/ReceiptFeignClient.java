package com.locket.user.feign;

import com.locket.user.domain.budget.dto.BudgetFeedbackRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "receipt-service", url = "${receipt.service.url}")  // application.yml에서 설정
public interface ReceiptFeignClient {
    @PostMapping("/api/feedback/generate")
    String generateFeedback(@RequestBody BudgetFeedbackRequest request);
}