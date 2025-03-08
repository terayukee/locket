package com.ssafy.locket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class LocketDiscoveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(LocketDiscoveryApplication.class, args);
	}

}
