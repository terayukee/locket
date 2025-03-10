package com.locket.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserTestController {
    private final PaymentClient paymentClient;

    @GetMapping("/test")
    public String testUser() {
        return "User Service is Running!";
    }

    @GetMapping("/call-payment")
    public String callPaymentService() {
        String paymentResponse = paymentClient.getPaymentStatus();
        return "User Service called Payment Service: " + paymentResponse;
    }
}
