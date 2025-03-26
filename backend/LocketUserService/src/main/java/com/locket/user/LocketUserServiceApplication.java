package com.locket.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {
        "com.locket.user",
        "com.locket.common" // ✅ 공통 유틸 포함
})
@EnableFeignClients // FeignClient 활성화
public class LocketUserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LocketUserServiceApplication.class, args);
    }
}
