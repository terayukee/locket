package com.locket.user.feign;

import com.locket.user.domain.budget.dto.BudgetFeedbackRequest;
import com.locket.user.domain.budget.dto.BudgetFeedbackResponse;
import com.locket.user.domain.budget.dto.ReceiptFeedbackResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "receipt-service", url = "${receipt.service.url}")
public interface ReceiptFeignClient {
    @PostMapping("/api/feedback/generate")
    ReceiptFeedbackResponse generateFeedback(@RequestBody BudgetFeedbackRequest request);
}