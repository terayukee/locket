package com.locket.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.locket.apigateway",
		"com.locket.common" // ✅ 공통 유틸 포함
})
public class LocketApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(LocketApiGatewayApplication.class, args);
	}

}
