package com.crpt.Crypto.Configuration;


import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BinanceFeignConfig {
    @Bean
    public RequestInterceptor apiKeyInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("X-MBX-APIKEY", "");
            requestTemplate.header("Content-Type", "application/json");
        };
    }
}