package com.crpt.Crypto;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@OpenAPIDefinition
@EnableFeignClients
public class CryptoDashboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptoDashboardApplication.class, args);
	}

}
