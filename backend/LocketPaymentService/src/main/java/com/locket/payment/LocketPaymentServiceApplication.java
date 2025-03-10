package com.locket.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient
public class LocketPaymentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LocketPaymentServiceApplication.class, args);
    }
}

@RestController
@RequestMapping("/api/payment")
class PaymentController {
    @GetMapping("/test")
    public String testPayment() {
        return "Payment Service is Running!";
    }
}
