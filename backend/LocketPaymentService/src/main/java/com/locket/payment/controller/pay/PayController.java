package com.locket.payment.controller.pay;

import com.locket.payment.service.pay.PayService;
import com.locket.payment.domain.pay.dto.QrPaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PayController {
    private final PayService payService;

    @PostMapping("/qr")
    public String processQrPayment(@RequestBody QrPaymentRequest request) {
        return payService.processQrPayment(request);
    }
}
