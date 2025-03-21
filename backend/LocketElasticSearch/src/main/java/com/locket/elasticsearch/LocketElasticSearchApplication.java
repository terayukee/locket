package com.locket.elasticsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients // FeignClient 활성화
public class LocketElasticSearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(LocketElasticSearchApplication.class, args);
	}

}
