package com.locket.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient
public class LocketUserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LocketUserServiceApplication.class, args);
    }
}

@RestController
@RequestMapping("/api/user")
class UserController {
    @GetMapping("/test")
    public String testUser() {
        return "User Service is Running!";
    }
}
